package com.example.musicplayerapp.data.repository

import com.example.musicplayerapp.data.database.dao.FavoriteDao
import com.example.musicplayerapp.data.database.dao.MusicTrackDao
import com.example.musicplayerapp.data.database.entities.FavoriteEntity
import com.example.musicplayerapp.data.database.entities.MusicTrackEntity
import com.example.musicplayerapp.data.local.MusicMediaStoreDataSource

class MusicRepository(
    private val musicTrackDao: MusicTrackDao,
    private val favoriteDao: FavoriteDao,
    private val musicMediaStoreDataSource: MusicMediaStoreDataSource
) {
    suspend fun getAllTracks() = musicMediaStoreDataSource.getAllTracks()

    suspend fun insertTracks(tracks: List<MusicTrackEntity>) {
        musicTrackDao.insertTracks(tracks)
    }

    suspend fun getTrackById(id: Long) = musicTrackDao.getTrackById(id)

    // Favoritos
    fun getFavorites() = favoriteDao.getFavoriteTracks()
    suspend fun addFavorite(trackId: Long) = favoriteDao.addFavorite(FavoriteEntity(
        trackId = trackId
    ))
    suspend fun removeFavorite(trackId: Long) = favoriteDao.removeFavorite(trackId)
    suspend fun isFavorite(trackId: Long) = favoriteDao.isFavorite(trackId)
}

