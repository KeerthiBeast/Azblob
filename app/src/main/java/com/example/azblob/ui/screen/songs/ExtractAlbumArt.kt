package com.example.azblob.ui.screen.songs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log

//Get the cover art using the uri of the file
fun extractAlbumArt(context: Context, uri: Uri?): Bitmap? {
    if (uri == null) return null

    try {
        MediaMetadataRetriever().use { mmr ->
            mmr.setDataSource(context, uri)
            val coverArt = mmr.embeddedPicture?.let { bytes->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
            return coverArt
        }
    } catch(e: Exception) {
        Log.d("Album Art Error", e.toString())
        return null
    }
}