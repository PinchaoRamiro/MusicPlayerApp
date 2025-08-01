package com.example.musicplayerapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayerapp.data.database.entities.MusicTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<MusicTrackEntity>)

    @Query("SELECT * FROM music_tracks")
    fun getAllTracks(): Flow<List<MusicTrackEntity>>

    @Query("SELECT * FROM music_tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: Long): MusicTrackEntity?

    @Query("DELETE FROM music_tracks")
    suspend fun deleteAllTracks()
}
