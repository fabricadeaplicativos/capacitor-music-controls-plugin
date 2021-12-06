package com.selcip.capacitor.MusicControl.Models

import android.util.Log
import com.getcapacitor.JSObject

data class TrackInfo(val data: JSObject) {
    var track = data.getString("track")
    var url = data.getString("url" )
    var album = data.getString("album")
    var artist = data.getString("artist")

    init {
        // Log.i(TAG, "track info: $data")
    }

    companion object {
        private const val TAG = "[CMC] TrackInfo"
    }
}
