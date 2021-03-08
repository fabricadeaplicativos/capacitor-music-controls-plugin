package com.selcip.capacitor.MusicControl

import android.util.Log
import com.getcapacitor.JSObject

data class TrackInfo(val data: JSObject) {
    var artist: String = data.getString("artist")
    var album: String? = data.getString("album")
    var track: String = data.getString("track")
    var ticker: String? = data.getString("ticker")
    var cover: String = data.getString("cover")
    var smallIcon: String? =  data.getString("smallIcon")
    var isPlaying: Boolean = data.getBoolean("isPlaying", true)
    var hasPrevious: Boolean = data.getBoolean("hasPrevious", true)
    var hasNext: Boolean = data.getBoolean("hasNext", true)
    var hasClose: Boolean = data.getBoolean("hasClose", true)

    init {
        Log.i(TAG, "Track info: $data")
    }

    companion object {
        private const val TAG = "[CMC] TrackInfo"
    }
}
