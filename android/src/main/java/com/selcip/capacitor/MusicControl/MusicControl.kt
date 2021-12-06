package com.selcip.capacitor.MusicControl

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission
import com.getcapacitor.annotation.PermissionCallback
import com.selcip.capacitor.MusicControl.Models.TrackInfo
import org.json.JSONException
import java.net.HttpURLConnection
import java.net.URL

@CapacitorPlugin(
    name = "MusicControl",
    permissions = [Permission(strings = [Manifest.permission.WAKE_LOCK], alias = "wake_lock")]
)
class MusicControl : Plugin() {
    private lateinit var trackInfo: TrackInfo
    private lateinit var notification: MusicControlNotification;
    lateinit var mediaSession: MediaSessionCompat

    private var mediaPlayer: MediaPlayer? = null
    private var setupDone: Boolean = false;
    private var mainHandler = Handler(Looper.getMainLooper())
    private var broadcastReceiver: MusicControlsBroadcastReceiver? = null
    private var isBroadcastRegistered = false;
    private var mediaSessionCallback: MediaSessionCallback? = null
    private var mediaButtonPendingIntent: PendingIntent? = null
    private var canAccessMediaButton: Boolean = false
    private var mConnection: ServiceConnection? = null

    override fun load() {
        if (!setupDone) {
            setup()
        }
    }

    fun setup() {
        notification = MusicControlNotification(context, NOTIFICATION_ID, this)

        if (isBroadcastRegistered) {
            context.unregisterReceiver(broadcastReceiver)
        }

        broadcastReceiver = MusicControlsBroadcastReceiver(this);
        registerBroadcaster(broadcastReceiver);
        isBroadcastRegistered = true;

        if (!this::mediaSession.isInitialized) {
            mediaSession = MediaSessionCompat(context, "music-media-session")
            setMediaPlaybackState(PlaybackState.STATE_PLAYING);
            mediaSession.isActive = true

            mediaSessionCallback = MediaSessionCallback(this);
            mediaSession.setCallback(mediaSessionCallback);
        }

        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            mediaButtonPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent("music-controls-media-button"),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            registerMediaButtonReceiver()
        } catch (e: java.lang.Exception) {
            canAccessMediaButton = false
            e.printStackTrace()
        }

//        val newMConnection: ServiceConnection = object : ServiceConnection {
//            override fun onServiceConnected(className: ComponentName?, binder: IBinder) {
//                Log.i(TAG, "onServiceConnected")
//                val service: MusicControlBackground =
//                    (binder as MusicControlBackground.KillBinder).service as MusicControlBackground
//                notification.setKillerService(service)
//                service.startService(Intent(activity, MusicControlBackground::class.java))
//                Log.i(TAG, "service Started")
//            }
//
//            override fun onServiceDisconnected(className: ComponentName?) {
//                Log.i(TAG, "service Disconnected")
//            }
//        }

//        val startServiceIntent = Intent(activity, MusicControlBackground::class.java)
//        startServiceIntent.putExtra("notificationID", NOTIFICATION_ID)
//        activity.bindService(startServiceIntent, newMConnection, Context.BIND_AUTO_CREATE)

        setupDone = true
    }

    @PluginMethod
    fun create(call: PluginCall) {
        if (getPermissionState("wake_lock") != PermissionState.GRANTED) {
            requestPermissionForAlias("wake_lock", call, "mediaPlayerPermissionsCallback");
        }

        if (!setupDone) {
            setup()
        }

        try {
            createMediaPlayer(call)
            setMetadata()
            call.resolve()
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
//            context.unregisterReceiver(broadcastReceiver)
//            isBroadcastRegistered = false;
            notification.destroy()
            stopMusic();
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
        call.resolve()
    }

    @PluginMethod
    fun updateIsPlaying(call: PluginCall) {
        try {
            val isPlaying = call.getBoolean("isPlaying")
            Log.i(TAG, "Manually setting isPlaying: $isPlaying")

            if (isPlaying == null) {
                if (mediaPlayer?.isPlaying == true) pauseMusic() else playMusic()
            } else {
                Log.i(TAG, "manual $isPlaying")
                if (isPlaying) pauseMusic() else playMusic()
            }
        } catch (e: JSONException) {
            println("toString(): $e")
            println("getMessage(): " + e.message)
            println("StackTrace: ")
            e.printStackTrace()
            call.reject("error in updateIsPlaying")
        }
    }

    @PluginMethod
    fun togglePlayPause(call: PluginCall) {
        Log.i(TAG, "Toggle play/pause")
        if (mediaPlayer?.isPlaying == true) pauseMusic() else playMusic()

        val ret = JSObject()
        ret.put("isPlaying", mediaPlayer?.isPlaying == true)
        call.resolve(ret)
    }


    fun setMetadata() {
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, trackInfo.track)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, trackInfo.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, trackInfo.album)

//        val cover: Bitmap? = getBitmapFromURL(trackInfo.cover!!)

//        println("teste: " + covers.size)

//        if (cover != null) {
//            metadata.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, cover)
//            metadata.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, cover)
//        }

        mediaSession.setMetadata(metadata.build())
    }

    //
    private fun registerMediaButtonReceiver() {
        this.mediaSession.setMediaButtonReceiver(mediaButtonPendingIntent)
    }

    private fun setMediaPlaybackState(state: Int) {
        val playBackState = PlaybackStateCompat.Builder()
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playBackState.setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
            )
            playBackState.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
        } else {
            playBackState.setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
            )
            playBackState.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
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

    fun notifyWebview(ret: JSObject, eventName: String = "controlsNotification") {
        Log.i(TAG, "$eventName fired " + ret.getString("message"))
        notifyListeners(eventName, ret)
    }

    private fun createMediaPlayer(call: PluginCall) {
        if (mediaPlayer?.isPlaying == true) {
            Log.i(TAG, "there is a music playing, stopping it")
            stopCurrentDurationCounter()
            pauseMusic()
        }

        //track current position every second
//      mainHandler.post(object : Runnable {
//          override fun run() {
//              Log.i(TAG, "Current position: ${mediaPlayer?.currentPosition} Total: ${mediaPlayer?.duration}")
//              mainHandler.postDelayed(this, 1000)
//          }
//      })

        trackInfo = TrackInfo(call.data);
        val url = trackInfo.url

        Log.i(TAG, "creating a new media player $url")

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            setDataSource(url)
            prepareAsync()
        }

        mediaPlayer?.setOnPreparedListener {
            Log.i(TAG, "music prepared, full duration is ${mediaPlayer?.duration}")
            playMusic()

            val showNotification = config.getBoolean("showNotification", true)

            if (showNotification) {
                notification.createNotification(trackInfo)
                notification.showNotification()
            }
        }

        mediaPlayer?.setOnCompletionListener {
            val ret = JSObject()
            ret.put("isPlaying", mediaPlayer?.isPlaying == true)
            ret.put("message", "song finished")
            notifyWebview(ret, "songFinished")
            mainHandler.removeCallbacksAndMessages(null)
        }
    }

    fun playMusic() {
        mediaPlayer?.start();
        notification.isPlaying = mediaPlayer?.isPlaying == true

        val ret = JSObject()
        ret.put("isPlaying", mediaPlayer?.isPlaying == true)
        ret.put("message", "playing music")
        notifyWebview(ret, "isPlaying")
    }

    fun pauseMusic() {
        mediaPlayer?.pause();
        notification.isPlaying = mediaPlayer?.isPlaying == true

        val ret = JSObject()
        ret.put("isPlaying", mediaPlayer?.isPlaying == true)
        ret.put("message", "pausing music")
        notifyWebview(ret, "isPlaying")
    }

    private fun stopMusic() {
        mediaPlayer?.pause();
        mediaPlayer?.release()
        mediaPlayer = null;

        val ret = JSObject()
        ret.put("isPlaying", mediaPlayer?.isPlaying == true)
        ret.put("message", "pausing music")
        notifyWebview(ret, "isPlaying")
    }

    private fun stopCurrentDurationCounter() {
        mainHandler.removeCallbacksAndMessages(null)
    }

    @PermissionCallback
    fun mediaPlayerPermissionsCallback(call: PluginCall) {
        Log.i(TAG, "callbackzada")
    }

    //
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