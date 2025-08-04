package com.example.musicplayerapp.data.repository

import android.util.Log
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
        Log.d("PlaylistRepository", "Creating playlist with name: $name")
        val id = playlistDao.createPlaylist(PlaylistEntity(name = name))
        Log.d("PlaylistRepository", "Playlist created with ID: $id")
        return id
    }

    suspend fun deletePlaylistById(playlistId: Long) {
        Log.d("PlaylistRepository", "Deleting playlist with ID: $playlistId")
        playlistDao.deletePlaylistById(playlistId)
    }

    fun getPlaylistWithTracks(playlistId: Long) =
        playlistTrackDao.getPlaylistWithTracks(playlistId).also {
            Log.d("PlaylistRepository", "Fetching playlist with tracks for ID: $playlistId")
        }

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: String) {
        Log.d("PlaylistRepository", "Adding track $trackId to playlist $playlistId")
        playlistTrackDao.addTrackToPlaylist(
            PlaylistTrackCrossRef(playlistId, trackId)
        )
        Log.d("PlaylistRepository", "Track $trackId added to playlist $playlistId")
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        Log.d("PlaylistRepository", "Removing track $trackId from playlist $playlistId")
        playlistTrackDao.removeTrackFromPlaylist(playlistId, trackId)
        Log.d("PlaylistRepository", "Track $trackId removed from playlist $playlistId")
    }
}
