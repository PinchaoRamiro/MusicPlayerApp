package com.example.musicplayerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.database.entities.PlaylistEntity
import com.example.musicplayerapp.data.database.entities.PlaylistWithCount
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.domain.usecase.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistUiState(
    val playlists: List<PlaylistWithCount> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    application: Application,
    private val playlistUseCases: PlaylistUseCases
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    init {
        observePlaylists()
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            playlistUseCases.getPlaylists()
                .collectLatest { playlists ->
                    _uiState.value = PlaylistUiState(
                        playlists = playlists,
                        isLoading = false
                    )
                }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "El nombre no puede estar vacío")
                return@launch
            }

            val result = playlistUseCases.createPlaylist(name)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Error al crear playlist"
                )
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val result = playlistUseCases.deletePlaylist(playlist.id)
//            if (result.isFailure) {
//                _uiState.value = _uiState.value.copy(
//                    error = result.exceptionOrNull()?.message ?: "Error al eliminar playlist"
//                )
//            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist, track: MusicTrack) {
        viewModelScope.launch {
            val result = playlistUseCases.addTrackToPlaylist(playlist.id.toLong(), track.id.toLong())
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Error al agregar canción"
                )
            }
        }
    }

    fun getPlaylistTracks(playlistId: Long): StateFlow<List<MusicTrack>> {
        val tracksFlow = MutableStateFlow<List<MusicTrack>>(emptyList())
        viewModelScope.launch {
            playlistUseCases.getPlaylistWithTracks(playlistId)
        }
        return tracksFlow.asStateFlow()
    }
}
