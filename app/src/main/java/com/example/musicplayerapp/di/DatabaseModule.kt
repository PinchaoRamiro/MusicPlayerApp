package com.example.musicplayerapp.di

import android.content.Context
import androidx.room.Room
import com.example.musicplayerapp.data.database.MusicDatabase
import com.example.musicplayerapp.data.database.dao.FavoriteDao
import com.example.musicplayerapp.data.database.dao.MusicTrackDao
import com.example.musicplayerapp.data.database.dao.PlaylistDao
import com.example.musicplayerapp.data.database.dao.PlaylistTrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "music_db"
        ).build()
    }

    @Provides
    fun provideMusicTrackDao(db: MusicDatabase): MusicTrackDao = db.musicTrackDao()

    @Provides
    fun provideFavoriteDao(db: MusicDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun providePlaylistDao(db: MusicDatabase): PlaylistDao = db.playlistDao()

    @Provides
    fun providePlaylistTrackDao(db: MusicDatabase): PlaylistTrackDao = db.playlistTrackDao()
}
