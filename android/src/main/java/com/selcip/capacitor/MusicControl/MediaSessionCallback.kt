package com.selcip.capacitor.MusicControl

import android.content.Intent
import android.media.session.MediaSession

import android.os.Bundle

import android.util.Log
import android.view.KeyEvent
import com.getcapacitor.JSObject


class MediaSessionCallback(private val musicControls: MusicControl) : MediaSession.Callback() {
    override fun onPlay() {
        super.onPlay()
        Log.i(TAG, "music-controls-media-button-play")
        val ret = JSObject()
        ret.put("message", "music-controls-media-button-play")
        musicControls.notify(ret)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "music-controls-media-button-pause")
        val ret = JSObject()
        ret.put("message", "music-controls-media-button-pause")
        musicControls.notify(ret)
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        Log.i(TAG, "music-controls-media-button-next")
        val ret = JSObject()
        ret.put("message", "music-controls-media-button-next")
        musicControls.notify(ret)
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        Log.i(TAG, "music-controls-media-button-previous")
        val ret = JSObject()
        ret.put("message", "music-controls-media-button-previous")
        musicControls.notify(ret)
    }

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
        super.onPlayFromMediaId(mediaId, extras)
    }

    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
        val event: KeyEvent = mediaButtonIntent.extras!![Intent.EXTRA_KEY_EVENT] as KeyEvent?
                ?: return super.onMediaButtonEvent(mediaButtonIntent)

        Log.i(TAG, "Evento: $event")

        return true
    }

    companion object {
        private const val TAG = "[CMC] MSessionCallback"
    }

}

