package com.example.musicplayerapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: String,
    val name: String,
    val tracks: List<MusicTrack>
) : Parcelable