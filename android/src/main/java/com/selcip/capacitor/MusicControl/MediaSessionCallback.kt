package com.selcip.capacitor.MusicControl

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import com.getcapacitor.JSObject


class MediaSessionCallback(private val musicControls: MusicControl) :
    MediaSessionCompat.Callback() {
    override fun onPlay() {
        super.onPlay()
        val ret = JSObject()
        ret.put("message", "Play button pressed")
        ret.put("action", "play")
        musicControls.notifyWebview(ret, "mediaSessionActions")
    }

    override fun onPause() {
        super.onPause()
        val ret = JSObject()
        ret.put("message", "Pause button pressed")
        ret.put("action", "pause")
        musicControls.notifyWebview(ret, "mediaSessionActions")
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        val ret = JSObject()
        ret.put("message", "Next button pressed")
        ret.put("action", "next")
        musicControls.notifyWebview(ret, "mediaSessionActions")
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        val ret = JSObject()
        ret.put("message", "Previous button pressed")
        ret.put("action", "previous")
        musicControls.notifyWebview(ret, "mediaSessionActions")
    }

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
        super.onPlayFromMediaId(mediaId, extras)
    }

    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
        val event: KeyEvent = mediaButtonIntent.extras!![Intent.EXTRA_KEY_EVENT] as KeyEvent?
            ?: return super.onMediaButtonEvent(mediaButtonIntent)

        Log.i(TAG, "media button event: $event")

        return true
    }

    companion object {
        private const val TAG = "[CMC] MediaSessionCallback"
    }

}

