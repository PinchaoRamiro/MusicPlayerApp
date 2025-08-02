package com.example.musicplayerapp.data.database.entities

import android.R
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_tracks")
data class MusicTrackEntity(
    @PrimaryKey val trackId: String, // ID único del MediaStore
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Long,
    val data: String
)