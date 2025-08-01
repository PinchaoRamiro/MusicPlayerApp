package com.example.musicplayerapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicplayerapp.data.database.dao.FavoriteDao
import com.example.musicplayerapp.data.database.dao.MusicTrackDao
import com.example.musicplayerapp.data.database.dao.PlaylistDao
import com.example.musicplayerapp.data.database.dao.PlaylistTrackDao
import com.example.musicplayerapp.data.database.entities.FavoriteEntity
import com.example.musicplayerapp.data.database.entities.MusicTrackEntity
import com.example.musicplayerapp.data.database.entities.PlaylistTrackCrossRef
import com.example.musicplayerapp.data.database.entities.PlaylistEntity

@Database(
    entities = [
        MusicTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        FavoriteEntity::class
    ],
    version = 1
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicTrackDao(): MusicTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
    abstract fun favoriteDao(): FavoriteDao
}
