package com.selcip.capacitor.MusicControl

import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.IBinder
import android.util.Log
import com.getcapacitor.*
import com.selcip.capacitor.MusicControl.MusicControlBackground.KillBinder
import org.json.JSONException
import java.net.HttpURLConnection
import java.net.URL


@NativePlugin
class MusicControl : Plugin() {
    private lateinit var trackInfo: TrackInfo
    private var broadcastReceiver: MusicControlsBroadcastReceiver? = null
    private var isBroadcastRegistered = false;
    private var mediaSessionCallback: MediaSessionCallback? = null
    private var mediaButtonPendingIntent: PendingIntent? = null

    private lateinit var notification: MusicControlNotification;
    lateinit var mediaSession: MediaSession
    private var canAccessMediaButton: Boolean = false
    var covers = mutableListOf<Cover>()
    private var mConnection: ServiceConnection? = null

    fun setup() {
        notification = MusicControlNotification(context, NOTIFICATION_ID, this)

        if (isBroadcastRegistered) {
            context.unregisterReceiver(broadcastReceiver)
        }

        broadcastReceiver = MusicControlsBroadcastReceiver(this);
        registerBroadcaster(broadcastReceiver);
        isBroadcastRegistered = true;

        if (!this::mediaSession.isInitialized) {
            mediaSession = MediaSession(context, "music-media-session")
            setMediaPlaybackState(PlaybackState.STATE_PLAYING);
            mediaSession.isActive = true

            mediaSessionCallback = MediaSessionCallback(this);
            mediaSession.setCallback(mediaSessionCallback);
        }

        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            mediaButtonPendingIntent = PendingIntent.getBroadcast(context, 0, Intent("music-controls-media-button"), PendingIntent.FLAG_UPDATE_CURRENT)
            registerMediaButtonReceiver()
        } catch (e: java.lang.Exception) {
            canAccessMediaButton = false
            e.printStackTrace()
        }

        val newMConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, binder: IBinder) {
                Log.i(TAG, "onServiceConnected")
                val service: MusicControlBackground = (binder as KillBinder).service as MusicControlBackground
                notification.setKillerService(service)
                service.startService(Intent(activity, MusicControlBackground::class.java))
                Log.i(TAG, "service Started")
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                Log.i(TAG, "service Disconnected")
            }
        }

        val startServiceIntent = Intent(activity, MusicControlBackground::class.java)
        startServiceIntent.putExtra("notificationID", NOTIFICATION_ID)
        activity.bindService(startServiceIntent, newMConnection, Context.BIND_AUTO_CREATE)
    }

    @PluginMethod
    fun create(call: PluginCall) {
        setup()

        try {
            trackInfo = TrackInfo(call.data);
            setMetadata()
            notification.trackInfo = trackInfo
            call.success()

        } catch (error: JSONException) {
            Log.i(TAG, "Error creating notification")
            Log.i(TAG, "$error")
            call.reject("There was an error creating the notification")
        }
    }

    @PluginMethod
    fun destroy(call: PluginCall) {
        Log.i(TAG, "Destroying notification")

        try {
            context.unregisterReceiver(broadcastReceiver)
            isBroadcastRegistered = false;
            notification.destroy()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        if (mConnection != null) {
            val stopServiceIntent = Intent(activity, MusicControlBackground::class.java)
            activity.unbindService(mConnection!!)
            activity.stopService(stopServiceIntent)
            mConnection = null
        }

        Log.i(TAG, "Notification destroyed")
        call.success()
    }

    @PluginMethod
    fun updateIsPlaying(call: PluginCall) {
        Log.i(TAG, "atualizando playing ${call.data.getBool("isPlaying")}")
        try {
            val isPlaying = call.data.getBool("isPlaying")
            notification.isPlaying = isPlaying
            if (isPlaying) setMediaPlaybackState(PlaybackState.STATE_PLAYING) else setMediaPlaybackState(PlaybackState.STATE_PAUSED)
            call.success()
        } catch (e: JSONException) {
            println("toString(): $e")
            println("getMessage(): " + e.message)
            println("StackTrace: ")
            e.printStackTrace()
            call.reject("error in updateIsPlaying")
        }
    }

    fun setMetadata() {
        val metadata = MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, trackInfo.track)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, trackInfo.artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, trackInfo.album)

        val cover: Bitmap? = getBitmapFromURL(trackInfo.cover)


        for (action in covers) {

        }

        covers.add(Cover(trackInfo.cover))


        println("teste: " + covers.size)

        if (cover != null) {
            metadata.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, cover)
            metadata.putBitmap(MediaMetadata.METADATA_KEY_ART, cover)
        }

        mediaSession.setMetadata(metadata.build())
    }

    private fun registerMediaButtonReceiver() {
        this.mediaSession.setMediaButtonReceiver(mediaButtonPendingIntent)
    }

    private fun setMediaPlaybackState(state: Int) {
        val playBackState = PlaybackState.Builder()
        if (state == PlaybackState.STATE_PLAYING) {
            playBackState.setActions(PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackState.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackState.ACTION_PLAY_FROM_SEARCH)
            playBackState.setState(state, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1.0f)
        } else {
            playBackState.setActions(PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_PLAY or PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackState.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackState.ACTION_PLAY_FROM_SEARCH)
            playBackState.setState(state, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0f)
        }
        this.mediaSession.setPlaybackState(playBackState.build())
    }

    private fun unregisterMediaButtonReceiver() {
        this.mediaSession.setMediaButtonReceiver(null)
    }

    private fun getBitmapFromURL(stringUrl: String): Bitmap? {
        return try {
            Log.i(TAG, "Downloading cover: $stringUrl")
            val url = URL(stringUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun notify(ret: JSObject) {
        Log.i(TAG, "controlsNotification fired " + ret.getString("message"))
        notifyListeners("controlsNotification", ret)
    }

    private fun registerBroadcaster(broadcastReceiver: MusicControlsBroadcastReceiver?) {
        context.registerReceiver(broadcastReceiver, IntentFilter("music-controls-previous"))
        context.registerReceiver(broadcastReceiver, IntentFilter("music-controls-pause"))
        context.registerReceiver(broadcastReceiver, IntentFilter("music-controls-play"))
        context.registerReceiver(broadcastReceiver, IntentFilter("music-controls-next"))
        context.registerReceiver(broadcastReceiver, IntentFilter("music-controls-media-button"))
        context.registerReceiver(broadcastReceiver, IntentFilter("music-controls-destroy"))

        // Listen for headset plug/unplug
        context.registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_HEADSET_PLUG))
    }

    companion object {
        private const val TAG = "[CMC] Main Service"
        private const val CHANNEL_ID = "CMCNotification"
        private const val NOTIFICATION_ID = 7824
    }
}