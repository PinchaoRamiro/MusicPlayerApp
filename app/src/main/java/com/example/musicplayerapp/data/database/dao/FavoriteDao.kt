package com.example.musicplayerapp.data.database.dao
import androidx.room.*
import com.example.musicplayerapp.data.database.entities.FavoriteEntity
import com.example.musicplayerapp.data.database.entities.MusicTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun removeFavorite(trackId: Long)

    @Query("""
        SELECT music_tracks.* FROM music_tracks 
        INNER JOIN favorites ON music_tracks.trackId = favorites.trackId
    """)
    fun getFavoriteTracks(): Flow<List<MusicTrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: Long): Boolean
}
