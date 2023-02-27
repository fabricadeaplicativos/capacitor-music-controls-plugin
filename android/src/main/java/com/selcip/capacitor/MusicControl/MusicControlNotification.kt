package com.selcip.capacitor.MusicControl

import android.R.attr
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.selcip.capacitor.MusicControl.Models.TrackInfo
import com.selcip.capacitor.MusicControl.capacitormusiccontrol.R
import java.net.HttpURLConnection
import java.net.URL
import androidx.media.app.NotificationCompat as MediaNotificationCompat
import com.getcapacitor.plugin.util.AssetUtil

import com.getcapacitor.Logger.config
import android.R.attr.smallIcon
import com.getcapacitor.PluginConfig


@SuppressLint("ServiceCast")
class MusicControlNotification(
    private val context: Context,
    private val NOTIFICATION_ID: Int,
    private val musicControls: MusicControl
) {
    private var trackInfo: TrackInfo? = null
    private lateinit var cover: Bitmap
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    var isPlaying: Boolean = true
        set(value) {
            Log.i(TAG, "changing isplaying on notification")
            field = value
            createNotification(trackInfo)
            showNotification()
        }

    init {
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel()
        }

        cover = BitmapFactory.decodeResource(context.resources, R.drawable.cmc_fixed_cover)
    }

    fun showNotification() {
        notificationManager!!.notify(NOTIFICATION_ID, notificationBuilder!!.build())
    }

    fun createNotification(newTrackInfo: TrackInfo?) {
        trackInfo = newTrackInfo

        val prevPendingIntent =
            PendingIntent.getBroadcast(
                context,
                1,
                Intent("music-controls-previous"),
                PendingIntent.FLAG_IMMUTABLE)

        val playOrPausePendingIntent =
            PendingIntent.getBroadcast(
                context,
                1,
                Intent(
                    if (isPlaying) {
                        "music-controls-pause"
                    } else {
                        "music-controls-play"
                    }
                ),
                PendingIntent.FLAG_IMMUTABLE
            )

        val nextPendingIntent =
            PendingIntent.getBroadcast(context, 1, Intent("music-controls-next"), PendingIntent.FLAG_IMMUTABLE)
        val deleteIntent =
            PendingIntent.getBroadcast(context, 1, Intent("music-controls-destroy"), PendingIntent.FLAG_IMMUTABLE)
        val openIntent =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, context.javaClass).setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LAUNCHER),
                PendingIntent.FLAG_IMMUTABLE
            )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                MediaNotificationCompat.MediaStyle()
                    .setMediaSession(musicControls.mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(R.drawable.cmc_previous_icon, "Previous", prevPendingIntent)
            .addAction(
                if (isPlaying) {
                    R.drawable.cmc_pause_icon
                } else {
                    R.drawable.cmc_play_icon
                }, "Pause", playOrPausePendingIntent
            )
            .addAction(R.drawable.cmc_next_icon, "Next", nextPendingIntent)
            .setDeleteIntent(deleteIntent)
            .setContentIntent(openIntent)
            .setContentTitle(trackInfo?.track)
            .setContentText(trackInfo?.artist)
            .setLargeIcon(cover)

        val customSmallIcon = AssetUtil.getResourceID(context, "cmc_small_icon", "drawable")

        if (customSmallIcon > 0) {
            builder.setSmallIcon(customSmallIcon)
        } else {
            builder.setSmallIcon(R.drawable.cmc_play_icon)
        }

        notificationBuilder = builder
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.resources.getString(R.string.channel_name)
            val descriptionText = context.resources.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            notificationManager!!.createNotificationChannel(channel)
        }
    }

    fun destroy() {
        notificationManager?.cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val TAG = "[CMC] Notification"
        private const val CHANNEL_ID = "CMCNotification"
    }
}