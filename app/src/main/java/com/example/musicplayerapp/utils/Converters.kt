package com.example.musicplayerapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.musicplayerapp.data.model.MusicTrack


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

// MediaItemExtensions.kt

fun MusicTrack.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setUri(this.data.toUri())
        .setMediaId(this.id)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this.title)
                .setArtist(this.artist)
                .setAlbumTitle(this.album)
                .setExtras(Bundle().apply {
                    putLong("duration", this@toMediaItem.duration)
                })
                .build()
        )
        .build()
}

fun MediaItem.toMusicTrack(): MusicTrack {
    val metadata = this.mediaMetadata
    val extras = metadata.extras

    return MusicTrack(
        id = this.mediaId,
        title = metadata.title?.toString().orEmpty(),
        artist = metadata.artist?.toString().orEmpty(),
        album = metadata.albumTitle?.toString(),
        duration = extras?.getLong("duration") ?: 0L,
        data = this.localConfiguration?.uri.toString()
    )
}
