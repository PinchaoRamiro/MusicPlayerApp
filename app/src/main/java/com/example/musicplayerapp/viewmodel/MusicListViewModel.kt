package com.example.musicplayerapp.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.domain.usecase.FavoriteUseCases
import com.example.musicplayerapp.domain.usecase.ScanMusicUseCase
import com.example.musicplayerapp.player.service.MediaPlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MusicListUiState(
    val isLoading: Boolean = true,
    val tracks: List<MusicTrack> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MusicListViewModel @Inject constructor(
    application: Application,
    private val scanMusicUseCase: ScanMusicUseCase,
    private val favoriteUseCases: FavoriteUseCases
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MusicListUiState())
    val uiState: StateFlow<MusicListUiState> = _uiState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<MusicTrack?>(null)
    val currentTrack: StateFlow<MusicTrack?> = _currentTrack.asStateFlow()

    private val _isShuffleModeEnabled = MutableStateFlow(false)
    val isShuffleModeEnabled: StateFlow<Boolean> = _isShuffleModeEnabled.asStateFlow()

    private val _favoriteTracks = MutableStateFlow<List<Long>>(emptyList())
    val favoriteTracks: StateFlow<List<Long>> = _favoriteTracks.asStateFlow()

    fun playTrack(context: Context, track: MusicTrack) {
        Log.d("MusicViewModel", "track: $track")
        MediaPlaybackService.play(context, track)
        _isPlaying.value = true
        _currentTrack.value = track
    }

    fun pauseTrack(context: Context) {
        MediaPlaybackService.pause(context)
        _isPlaying.value = false
    }

    fun nextTrack(context: Context) {
        MediaPlaybackService.next(context)
    }

    fun previousTrack(context: Context) {
        MediaPlaybackService.previous(context)
    }

    fun toggleShuffle(context: Context) {
        MediaPlaybackService.toggleShuffle(context)
        _isShuffleModeEnabled.value = !_isShuffleModeEnabled.value
    }

    fun toggleFavorite(trackId: String) {
        viewModelScope.launch {
            val isFav = favoriteUseCases.isFavorite(trackId)
            if (isFav) {
                favoriteUseCases.removeFavorite(trackId)
            } else {
                favoriteUseCases.addFavorite(trackId)
            }
            refreshFavorites()
        }
    }

    private fun refreshFavorites() {
        viewModelScope.launch {
            favoriteUseCases.getFavorites()
                .collect { list ->
                    _favoriteTracks.value = list.map { it.trackId.toLong() }
                }
        }
    }

    fun loadMusic() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val tracks = scanMusicUseCase()

                if (tracks.isNotEmpty()) {
                    MediaPlaybackService.setPlaylistOnly(getApplication(), tracks, startIndex = 0)
                }

                _uiState.value = MusicListUiState(
                    isLoading = false,
                    tracks = tracks
                )

                refreshFavorites()

            } catch (e: Exception) {
                _uiState.value = MusicListUiState(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
