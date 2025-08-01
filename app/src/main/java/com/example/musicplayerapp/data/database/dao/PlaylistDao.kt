package com.example.musicplayerapp.data.database.dao


import androidx.room.*
import com.example.musicplayerapp.data.database.entities.PlaylistEntity
import com.example.musicplayerapp.data.database.entities.PlaylistWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: PlaylistEntity): Long

    @Query("""
        SELECT p.playlistId, 
               p.name, 
               COUNT(pt.trackId) AS trackCount
        FROM playlists p
        LEFT JOIN playlist_track_cross_ref pt 
               ON p.playlistId = pt.playlistId
        GROUP BY p.playlistId
        ORDER BY p.createdAt DESC
    """)
    fun getAllPlaylists(): Flow<List<PlaylistWithCount>>

    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

}
