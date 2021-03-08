package com.selcip.capacitor.MusicControl

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import com.selcip.capacitor.MusicControl.capacitormusiccontrol.R
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class MusicControlNotification(private val context: Context, private val NOTIFICATION_ID: Int, private val musicControls: MusicControl) {
    private var notificationBuilder: Notification.Builder? = null
    var isPlaying: Boolean = false
        set(value) {
            if (isPlaying == trackInfo?.isPlaying && hasNotification()) {
                return  // Not recreate the notification with the same data
            }

            Log.i(TAG, "isPlaying was: ${trackInfo?.isPlaying}")
            field = value
            trackInfo?.isPlaying = value
            Log.i(TAG, "isPlaying is now: ${trackInfo?.isPlaying}")

            createBuilder()
            createNotification()
        }
    private var bitmapCover: Bitmap? = null
    var trackInfo: TrackInfo? = null
        set(value) {
            field = value

            if (bitmapCover == null) {
                trackInfo?.cover?.let { getCover(it) }
            }

            createBuilder()
            createNotification()
        }

    private var notificationManager: NotificationManager? = null
    var killer_service: WeakReference<MusicControlBackground>? = null

    init {
        Log.i(TAG, "iniciando notificacao")

        if (notificationManager == null) {
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            createNotificationChannel()
        } else {
            Log.i(TAG, "Notification manager already exists")
        }
    }

    private fun createNotification() {
        val notification = notificationBuilder!!.build()

        if (killer_service != null) {
            killer_service!!.get()?.setNotification(notification);
        }

        notificationManager!!.notify(NOTIFICATION_ID, notification)
    }

    fun setKillerService(s: MusicControlBackground) {
        killer_service = WeakReference(s)
    }

    private fun hasNotification(): Boolean {
        return killer_service != null && killer_service!!.get()!!.getNotification() != null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "capacitor-music-control"
            val description = "capacitor-music-control notification"
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.setSound(null, null)
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.description = description
            notificationManager!!.createNotificationChannel(notificationChannel)
        } else {
            Log.i(TAG, "Notification channel requires api greater than 26, current api is: ${Build.VERSION.SDK_INT}")
        }
    }

    private fun createBuilder() {
        Log.i(TAG, "Creating notification builder")

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            Notification.Builder(context)
        }

        val mediaStyle = Notification.MediaStyle().setMediaSession(musicControls.mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2)

        builder.setContentTitle(trackInfo?.track)
        builder.setContentText(trackInfo?.artist)
        builder.setTicker(trackInfo?.ticker)

        if (trackInfo?.cover?.isNotEmpty() == true && bitmapCover != null) {
            builder.setLargeIcon(bitmapCover)
        }

        builder.setVisibility(Notification.VISIBILITY_PUBLIC)
        builder.style = mediaStyle

        //dismiss intent
        builder.setOngoing(false)
        builder.setDeleteIntent(PendingIntent.getBroadcast(context, 1, Intent("music-controls-destroy"), 0))

        //tap to open
        builder.setContentIntent(
                PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, context.javaClass).setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
                        0
                )
        )

        if (trackInfo?.smallIcon != null) {
            val resourceId = getResourceId(trackInfo?.smallIcon, 0)
            val usePlayingIcon = resourceId == 0
            if (!usePlayingIcon) {
                builder.setSmallIcon(resourceId)
            } else {
                builder.setSmallIcon(R.drawable.cmc_play_icon)
            }
        } else {
            builder.setSmallIcon(R.drawable.cmc_play_icon)
        }

        if (trackInfo?.cover?.isEmpty() == true && this.bitmapCover != null) {
            builder.setLargeIcon(this.bitmapCover);
        }

        val actions = getActions()

        for (action in actions) {
            builder.addAction(action)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.style = Notification.MediaStyle().setMediaSession(musicControls.mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2)
        }

        notificationBuilder = builder
    }

    private fun getActions(): ArrayList<Notification.Action> {
        val actions: ArrayList<Notification.Action> = ArrayList()

        if (trackInfo?.hasPrevious == true) {
            actions.add(
                    Notification.Action.Builder(
                            R.drawable.cmc_previous_icon, "previous",
                            PendingIntent.getBroadcast(context, 1, Intent("music-controls-previous"), 0)
                    ).build()
            )
        }

        Log.i(TAG, "Actions is playing: $isPlaying")

        if (trackInfo?.isPlaying == true) {
            actions.add(
                    Notification.Action.Builder(
                            R.drawable.cmc_pause_icon, "pause",
                            PendingIntent.getBroadcast(context, 1, Intent("music-controls-pause"), 0)
                    ).build()
            )
        } else {
            actions.add(
                    Notification.Action.Builder(
                            R.drawable.cmc_play_icon, "play",
                            PendingIntent.getBroadcast(context, 1, Intent("music-controls-play"), 0)
                    ).build()
            )
        }

        if (trackInfo?.hasNext == true) {
            actions.add(
                    Notification.Action.Builder(
                            R.drawable.cmc_next_icon, "next",
                            PendingIntent.getBroadcast(context, 1, Intent("music-controls-next"), 0)
                    ).build()
            )
        }

        return actions
    }

    private fun getCover(url: String) {
        try {
            bitmapCover = getBitmapFromURL(url)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
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

    fun destroy() {
        Log.i(TAG, "Canceling...")

        if (this.killer_service !=null) {
            this.killer_service!!.get()?.setNotification(null);
        }

        this.notificationManager?.cancel(NOTIFICATION_ID)

        Log.i(TAG, "Cancelled")
    }

    private fun getResourceId(name: String?, fallback: Int): Int {
        return try {
            if (name?.isEmpty() == true) {
                return fallback
            }
            val resourceId: Int = context.resources.getIdentifier(name, "drawable", context.packageName)
            if (resourceId == 0) fallback else resourceId
        } catch (ex: Exception) {
            fallback
        }
    }

    companion object {
        private const val TAG = "[CMC] Notification"
        private const val CHANNEL_ID = "CMCNotification"
    }
}