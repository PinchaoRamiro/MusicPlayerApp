package com.example.musicplayerapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val favId: Long = 0,
    val trackId: String,
    val addedAt: Long = System.currentTimeMillis()
)
