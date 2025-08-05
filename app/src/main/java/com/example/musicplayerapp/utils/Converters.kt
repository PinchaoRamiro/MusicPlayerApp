package com.example.musicplayerapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever


fun formatDuration(durationMillis: Long): String {
    val minutes = (durationMillis / 1000) / 60
    val seconds = (durationMillis / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun extractAlbumArt(filePath: String): Bitmap? {
    if (filePath.isEmpty()) return null
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(filePath)
        val art = retriever.embeddedPicture
        if (art != null) {
            BitmapFactory.decodeByteArray(art, 0, art.size)
        } else {
            null // No hay imagen incrustada
        }
    } catch (e: Exception) {
        null
    } finally {
        retriever.release()
    }
}