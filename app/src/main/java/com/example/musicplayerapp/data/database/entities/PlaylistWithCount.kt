package com.example.musicplayerapp.data.database.entities

import androidx.room.ColumnInfo

data class PlaylistWithCount(
    val playlistId: Long,
    val name: String,
    @ColumnInfo(name = "trackCount") val trackCount: Int
)
