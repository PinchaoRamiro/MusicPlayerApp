package com.example.musicplayerapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    fun addTrackToPlaylist(playlist : Long, track: MusicTrack) {
        viewModelScope.launch {
            val result = playlistUseCases.addTrackToPlaylist(playlist, track.id)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Error al agregar canción"
                )
            }
        }
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        viewModelScope.launch {
            val result = playlistUseCases.removeTrackFromPlaylist(playlistId, trackId)
/*            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Error al eliminar canción"
                )
            }*/
            Log.d("PlaylistViewModel", "Track removed from playlist: $trackId")
        }
    }

    fun getPlaylistTracks(playlistId: Long): StateFlow<List<MusicTrack>> {
        val tracksFlow = MutableStateFlow<List<MusicTrack>>(emptyList())
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            playlistUseCases.getPlaylistWithTracks(playlistId).collect { playlistWithTracks ->
                val tracks = playlistWithTracks?.tracks?.map { trackEntity -> MusicTrack(
                    id = trackEntity.trackId,
                    title = trackEntity.title,
                    artist = trackEntity.artist,
                    album = trackEntity.album,
                    duration = trackEntity.duration,
                    data = trackEntity.data
                ) }
                if (tracks != null) {
                    tracksFlow.value = tracks
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                Log.d("PlaylistViewModel", "Playlist updated: $tracks")
            }
        }
        return tracksFlow.asStateFlow()
    }


}
