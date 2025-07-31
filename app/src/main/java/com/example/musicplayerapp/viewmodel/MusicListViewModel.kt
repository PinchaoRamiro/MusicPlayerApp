package com.example.musicplayerapp.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.domain.usecase.ScanMusicUseCase
import com.example.musicplayerapp.player.service.MediaPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MusicListUiState(
    val isLoading: Boolean = true,
    val tracks: List<MusicTrack> = emptyList(),
    val error: String? = null
)

class MusicListViewModel(
    application: Application,
    private val scanMusicUseCase: ScanMusicUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MusicListUiState())
    val uiState: StateFlow<MusicListUiState> = _uiState

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentTrack = MutableStateFlow<MusicTrack?>(null)
    val currentTrack: StateFlow<MusicTrack?> = _currentTrack.asStateFlow()

    private val _isShuffleModeEnabled = MutableStateFlow(false)
    val isShuffleModeEnabled: StateFlow<Boolean> = _isShuffleModeEnabled.asStateFlow()

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
        Log.d("MusicViewModel", "Requesting next track.")
        MediaPlaybackService.next(context)
    }

    fun previousTrack(context: Context) {
        Log.d("MusicViewModel", "Requesting previous track.")
        MediaPlaybackService.previous(context)
    }

    fun toggleShuffle(context: Context) {
        Log.d("MusicViewModel", "Toggling shuffle mode.")
        MediaPlaybackService.toggleShuffle(context)
        // La actualización del _isShuffleModeEnabled debería venir del servicio
        // para asegurar que el estado es consistente. Sin embargo, para una respuesta rápida
        // de la UI, podemos actualizarlo aquí temporalmente.
        // Lo ideal es que el ViewModel observe el estado de shuffle del MusicPlayerController.
        _isShuffleModeEnabled.value = !_isShuffleModeEnabled.value
    }

    fun loadMusic() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val tracks = scanMusicUseCase()

                if (tracks.isNotEmpty()) {
                    Log.d("MusicListModel", "Enviando PlayList a el service con setPlaylistOnly")
                    MediaPlaybackService.setPlaylistOnly(getApplication(), tracks, startIndex = 0)
                }

                _uiState.value = MusicListUiState(
                    isLoading = false,
                    tracks = tracks
                )
            } catch (e: Exception) {
                _uiState.value = MusicListUiState(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
