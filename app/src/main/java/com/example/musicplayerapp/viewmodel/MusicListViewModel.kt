package com.example.musicplayerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.domain.usecase.ScanMusicUseCase
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
    internal val musicServiceConnection: MusicServiceConnection
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MusicListUiState())
    val uiState: StateFlow<MusicListUiState> = _uiState.asStateFlow()

    val currentTrack: StateFlow<MusicTrack?> = musicServiceConnection.currentTrack
    val currentPosition: StateFlow<Long> = musicServiceConnection.currentPosition
    val currentDuration: StateFlow<Long> = musicServiceConnection.currentDuration
    val isPlaying: StateFlow<Boolean> = musicServiceConnection.isPlaying
    val isShuffleModeEnabled: StateFlow<Boolean> = musicServiceConnection.isShuffleEnabled

    val playlistId : StateFlow<Long?> = musicServiceConnection.playlistRec

    private val _favoriteTracks = MutableStateFlow<List<Long>>(emptyList())
    val favoriteTracks: StateFlow<List<Long>> = _favoriteTracks.asStateFlow()

    init {
        musicServiceConnection.connect()
    }

    fun playTrack(track: MusicTrack) = musicServiceConnection.play(track)
    fun pauseTrack() = musicServiceConnection.pause()
    fun nextTrack() = musicServiceConnection.next()
    fun previousTrack() = musicServiceConnection.previous()
    fun toggleShuffle() = musicServiceConnection.toggleShuffle()

    fun setPlaylist(tracks: List<MusicTrack>, startIndex: Int, playlistId: Long) {
        musicServiceConnection.setPlaylist(tracks, startIndex, playlistId)
    }

    fun loadMusic() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val tracks = scanMusicUseCase()
                if (tracks.isNotEmpty()) {
                    musicServiceConnection.setPlaylist(tracks, startIndex = 0, playlistId = -1)
                }
                _uiState.value = MusicListUiState(isLoading = false, tracks = tracks)
            } catch (e: Exception) {
                _uiState.value = MusicListUiState(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.disconnect()
    }
}
