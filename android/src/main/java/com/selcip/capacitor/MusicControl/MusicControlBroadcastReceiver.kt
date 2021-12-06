package com.selcip.capacitor.MusicControl

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.getcapacitor.JSObject

class MusicControlsBroadcastReceiver(private val musicControls: MusicControl) : BroadcastReceiver() {
    private var notificationBuilder: Notification.Builder? = null

    fun stopListening() {
        val ret = JSObject()
        ret.put("message", "music-controls-stop-listening")
        musicControls.notifyWebview(ret)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message: String? = intent.action
        val ret = JSObject()

        Log.i(TAG, "Message received: $message")

        when (message) {
            "music-controls-pause" -> {
                ret.put("message", "Pause button pressed")
                ret.put("action", "pause")
                musicControls.pauseMusic()
                musicControls.notifyWebview(ret, "mediaActions")
            }
            "music-controls-play" -> {
                ret.put("message", "Play button pressed")
                ret.put("action", "play")
                musicControls.playMusic()
                musicControls.notifyWebview(ret, "mediaActions")
            }
            "music-controls-next" -> {
                ret.put("message", "Next button pressed")
                ret.put("action", "next")
                musicControls.notifyWebview(ret, "mediaActions")
            }
            "music-controls-previous" -> {
                ret.put("message", "Previous button pressed")
                ret.put("action", "previous")
                musicControls.notifyWebview(ret, "mediaActions")
            }
            "music-controls-destroy" -> {
                ret.put("message", "Notification destroyed")
                ret.put("action", "destroy")
                musicControls.notifyWebview(ret, "mediaActions")
            }
        }
    }

    companion object {
        private const val TAG = "[CMC] BroadcastReceiver"
    }

}
