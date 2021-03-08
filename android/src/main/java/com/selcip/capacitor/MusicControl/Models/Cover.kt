package com.selcip.capacitor.MusicControl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.util.*

class Cover(val url: String) {
    private var bitmap: Bitmap?

    init {
        bitmap = getBitmapFromURL(url)
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
        } finally {
            Log.i(TAG, "Download completed: $stringUrl")
        }
    }

    fun shouldUpdate(newUrl: String): Boolean {
        return url == newUrl
    }

    companion object {
        private const val TAG = "[CMC] Cover"
    }
}