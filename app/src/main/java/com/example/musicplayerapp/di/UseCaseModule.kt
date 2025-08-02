package com.example.musicplayerapp.di

import android.content.Context
import com.example.musicplayerapp.data.local.MusicMediaStoreDataSource
import com.example.musicplayerapp.data.repository.MusicRepository
import com.example.musicplayerapp.data.repository.PlaylistRepository
import com.example.musicplayerapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun providePlaylistUseCases(repository: PlaylistRepository): PlaylistUseCases {
        return PlaylistUseCases(
            createPlaylist = CreatePlaylistUseCase(repository),
            getPlaylists = GetPlaylistsUseCase(repository),
            addTrackToPlaylist = AddTrackToPlaylistUseCase(repository),
            getPlaylistWithTracks = GetPlaylistWithTracksUseCase(repository),
            removeTrackFromPlaylist = RemoveTrackFromPlaylistUseCase(repository),
            deletePlaylist = DeletePlaylistUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideFavoriteUseCases(repository: MusicRepository): FavoriteUseCases {
        return FavoriteUseCases(
            addFavorite = AddFavoriteUseCase(repository),
            removeFavorite = RemoveFavoriteUseCase(repository),
            isFavorite = IsFavoriteUseCase(repository),
            getFavorites = GetFavoritesUseCase(repository)
        )
    }

    @Provides
    fun provideMediaStoreDataSource(
        @ApplicationContext context: Context
    ): MusicMediaStoreDataSource {
        return MusicMediaStoreDataSource(context)
    }

}
