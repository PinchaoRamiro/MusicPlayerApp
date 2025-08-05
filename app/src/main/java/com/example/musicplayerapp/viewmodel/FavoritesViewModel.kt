package com.example.musicplayerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.database.entities.MusicTrackEntity
import com.example.musicplayerapp.data.model.MusicTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.musicplayerapp.domain.usecase.FavoriteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<MusicTrack> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    application: Application,
    private val favoriteUseCases: FavoriteUseCases,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        refreshFavorites()
    }

    fun toggleFavorite(trackId: String) {
        if (trackId.isEmpty()) return
        viewModelScope.launch {
            if (favoriteUseCases.isFavorite(trackId)) {
                favoriteUseCases.removeFavorite(trackId)
            } else {
                favoriteUseCases.addFavorite(trackId)
            }
            refreshFavorites()
        }
    }


    private fun addFavorite(trackId: String) {
        viewModelScope.launch {
            favoriteUseCases.addFavorite(trackId)
            refreshFavorites()
        }
    }

    private fun removeFavorite(trackId: String) {
        viewModelScope.launch {
            favoriteUseCases.removeFavorite(trackId)
            refreshFavorites()
        }
    }

    private fun refreshFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            favoriteUseCases.getFavorites()
                .collect { list ->
                    _uiState.value = _uiState.value.copy(favorites = list.map{
                        it.toMusicTrack()
                    })
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
    }

    private fun MusicTrackEntity.toMusicTrack(): MusicTrack {
        return MusicTrack(
            id = trackId,
            title = title,
            artist = artist,
            album = album,
            duration = duration,
            data = data
        )
    }
}

