package com.example.musicplayerapp.data.database.entities


data class PlaylistWithCount(
    val playlistId: Long,
    val name: String,
    val trackCount: Int
)
