package com.example.musicplayerapp.data.database.dao

import androidx.room.*
import com.example.musicplayerapp.data.database.entities.PlaylistTrackCrossRef
import com.example.musicplayerapp.data.database.relations.PlaylistWithTracks
import com.example.musicplayerapp.data.database.relations.TrackWithPlaylists
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks>

    @Transaction
    @Query("SELECT * FROM music_tracks WHERE id = :trackId")
    fun getTrackWithPlaylists(trackId: Long): Flow<TrackWithPlaylists>
}
