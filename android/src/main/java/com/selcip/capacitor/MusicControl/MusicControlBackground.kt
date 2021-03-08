package com.selcip.capacitor.MusicControl

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import java.lang.ref.WeakReference


class MusicControlBackground: Service() {

    class KillBinder(val service: Service) : Binder()

    private var mNM: NotificationManager? = null
    private val mBinder: IBinder = KillBinder(this)
    // Partial wake lock to prevent the app from going to sleep when locked
    private var wakeLock: PowerManager.WakeLock? = null
    private var notification: WeakReference<Notification>? = null
    private var foregroundStarted = false


    override fun onBind(intent: Intent): IBinder {
        NOTIFICATION_ID = intent.getIntExtra("notificationID", 1)
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    fun setNotification(n: Notification?) {
        Log.i(TAG, "setNotification")
        if (notification != null) {
            if (n == null) {
                sleepWell(true)
            }
            notification = null
        }
        if (n != null) {
            notification = WeakReference<Notification>(n)
            keepAwake(wakeLock == null)
        }
    }

    fun getNotification(): Notification? {
        return if (notification != null) {
            notification!!.get()
        } else {
            null
        }
    }

    /**
     * Put the service in a foreground state to prevent app from being killed
     * by the OS.
     */
    private fun keepAwake(do_wakelock: Boolean) {
        if (notification != null && notification!!.get() != null && !foregroundStarted) {
            Log.i(TAG, "Starting ForegroundService")
            startForeground(NOTIFICATION_ID, notification!!.get())
            foregroundStarted = true
        }
        if (do_wakelock) {
            val pm: PowerManager? = getSystemService(POWER_SERVICE) as PowerManager?
            wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
            Log.i(TAG, "Acquiring LOCK")
            wakeLock!!.acquire(10 * 60 * 1000L /*10 minutes*/)
            if (wakeLock!!.isHeld) {
                Log.i(TAG, "wakeLock acquired")
            } else {
                Log.e(TAG, "wakeLock not acquired yet")
            }
        }
    }

    /**
     * Shared manager for the notification service.
     */
    private fun getNotificationManager(): NotificationManager? {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate")
        mNM = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        mNM?.cancel(NOTIFICATION_ID)
    }

    /**
     * Stop background mode.
     */
    private fun sleepWell(do_wakelock: Boolean) {
        Log.i(TAG, "Stopping WakeLock")
        if (foregroundStarted) {
            Log.i(TAG, "Stopping ForegroundService")
            stopForeground(true)
            foregroundStarted = false
            Log.i(TAG, "ForegroundService stopped")
        }
        mNM?.cancel(NOTIFICATION_ID)
        if (wakeLock != null && do_wakelock) {
            if (wakeLock!!.isHeld) {
                try {
                    wakeLock!!.release()
                    Log.i(TAG, "wakeLock released")
                } catch (e: Exception) {
                    Log.e(TAG, e.message)
                }
            } else {
                Log.i(TAG, "wakeLock not held")
            }
            wakeLock = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sleepWell(true)
    }

    companion object {
        private const val TAG = "cmc:NotifyKiller"
        private var NOTIFICATION_ID = 0
    }
}