package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.repository.MusicRepository
import javax.inject.Inject

data class FavoriteUseCases @Inject constructor(
    val addFavorite: AddFavoriteUseCase,
    val removeFavorite: RemoveFavoriteUseCase,
    val isFavorite: IsFavoriteUseCase,
    val getFavorites: GetFavoritesUseCase
)

class AddFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(trackId: String) = repository.addFavorite(trackId)
}

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(trackId: String) = repository.removeFavorite(trackId)
}

class IsFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(trackId: String) = repository.isFavorite(trackId)
}

class GetFavoritesUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke() = repository.getFavorites()
}
