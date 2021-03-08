package com.selcip.capacitor.MusicControl

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import com.getcapacitor.JSObject

class MusicControlsBroadcastReceiver(private val musicControls: MusicControl) : BroadcastReceiver() {
    private var notificationBuilder: Notification.Builder? = null

    fun stopListening() {
        val ret = JSObject()
        ret.put("message", "music-controls-stop-listening")
        musicControls.notify(ret)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message: String? = intent.action
        val ret = JSObject()

        Log.i(TAG, "Message received: $message")

        when (message) {
            "music-controls-pause" -> {
                ret.put("message", "music-controls-pause")
                musicControls.notify(ret)
            }
            "music-controls-play" -> {
                ret.put("message", "music-controls-play")
                musicControls.notify(ret)
            }
            "music-controls-next" -> {
                ret.put("message", "music-controls-next")
                musicControls.notify(ret)
            }
            "music-controls-previous" -> {
                ret.put("message", "music-controls-previous")
                musicControls.notify(ret)
            }
            "music-controls-destroy" -> {
                ret.put("message", "music-controls-destroy")
                musicControls.notify(ret)
            }
        }
//            val event: KeyEvent? = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
//
//            println(event!!.keyCode)
//            if (event.action == KeyEvent.ACTION_DOWN) {
//                when (event.keyCode) {
//                    KeyEvent.KEYCODE_MEDIA_NEXT -> {
//                        ret.put("message", "music-controls-media-button-next")
//                        musicControls.notify(ret)
//                    }
//                    KeyEvent.KEYCODE_MEDIA_PAUSE -> {
//                        ret.put("message", "music-controls-media-button-pause")
//                        musicControls.notify(ret)
//                    }
//                    KeyEvent.KEYCODE_MEDIA_PLAY -> {
//                        ret.put("message", "music-controls-media-button-play")
//                        musicControls.notify(ret)
//                    }
//                }
//            }
    }

    companion object {
        private const val TAG = "[CMC] BroadcastReceiver"
    }

}
