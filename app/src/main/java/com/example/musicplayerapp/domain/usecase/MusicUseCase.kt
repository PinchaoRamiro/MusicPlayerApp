package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.repository.MusicRepository

class GetAllTracksUseCase(private val repository: MusicRepository) {
    suspend operator fun invoke() = repository.getAllTracks()
}

class AddFavoriteUseCase(private val repository: MusicRepository) {
    suspend operator fun invoke(trackId: Long) = repository.addFavorite(trackId)
}

class RemoveFavoriteUseCase(private val repository: MusicRepository) {
    suspend operator fun invoke(trackId: Long) = repository.removeFavorite(trackId)
}
