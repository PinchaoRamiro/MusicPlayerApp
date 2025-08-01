package com.example.musicplayerapp.di

import com.example.musicplayerapp.data.repository.MusicRepository
import com.example.musicplayerapp.data.repository.PlaylistRepository
import com.example.musicplayerapp.data.database.dao.*
import com.example.musicplayerapp.data.local.MusicMediaStoreDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        musicTrackDao: MusicTrackDao,
        favoriteDao: FavoriteDao,
        musicMediaStoreDataSource: MusicMediaStoreDataSource
    ): MusicRepository {
        return MusicRepository(
            musicTrackDao, favoriteDao,
            musicMediaStoreDataSource
        )
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(
        playlistDao: PlaylistDao,
        playlistTrackDao: PlaylistTrackDao
    ): PlaylistRepository {
        return PlaylistRepository(playlistDao, playlistTrackDao)
    }
}
