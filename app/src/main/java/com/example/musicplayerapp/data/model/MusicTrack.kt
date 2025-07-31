package com.example.musicplayerapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Long,
    val data: String  // ruta de la cancion
) : Parcelable