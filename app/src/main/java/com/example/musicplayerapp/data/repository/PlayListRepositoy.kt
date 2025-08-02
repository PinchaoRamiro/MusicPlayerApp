package com.example.musicplayerapp.data.repository

import com.example.musicplayerapp.data.database.dao.PlaylistDao
import com.example.musicplayerapp.data.database.dao.PlaylistTrackDao
import com.example.musicplayerapp.data.database.entities.PlaylistEntity
import com.example.musicplayerapp.data.database.entities.PlaylistTrackCrossRef

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao
) {
    fun getAllPlaylists() = playlistDao.getAllPlaylists()

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.createPlaylist(PlaylistEntity(name = name))
    }

    suspend fun deletePlaylistById(playlistId: Long) {
        playlistDao.deletePlaylistById(playlistId)
    }

    fun getPlaylistWithTracks(playlistId: Long) =
        playlistTrackDao.getPlaylistWithTracks(playlistId)

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: String) {
        playlistTrackDao.addTrackToPlaylist(
            PlaylistTrackCrossRef(playlistId, trackId)
        )
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        playlistTrackDao.removeTrackFromPlaylist(playlistId, trackId)
    }
}
