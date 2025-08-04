package com.example.musicplayerapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.domain.usecase.ScanMusicUseCase
import com.example.musicplayerapp.domain.usecase.PlaylistUseCases
import com.example.musicplayerapp.viewmodel.MusicListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddToPlaylistUiState(
    val isLoading: Boolean = true,
    val tracks: List<MusicTrack> = emptyList(),
    val selectedTrackIds: Set<String> = emptySet()
)

@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
    private val scanMusicUseCase: ScanMusicUseCase,
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddToPlaylistUiState())
    val uiState: StateFlow<AddToPlaylistUiState> = _uiState

    fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val tracks = scanMusicUseCase()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                tracks = tracks
            )
        }
    }

    fun toggleSelection(trackId: String) {
        val currentSelection = _uiState.value.selectedTrackIds.toMutableSet()
        if (currentSelection.contains(trackId)) {
            currentSelection.remove(trackId)
        } else {
            currentSelection.add(trackId)
        }
        _uiState.value = _uiState.value.copy(selectedTrackIds = currentSelection)
    }

    fun addSelectedTracksToPlaylist(playlistId: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            val selectedTracks = _uiState.value.tracks.filter { it.id in _uiState.value.selectedTrackIds }
            selectedTracks.forEach { track ->
                Log.d("AddToPlaylistVM", "Adding track ${track.title} (${track.id}) to playlist $playlistId")
                playlistUseCases.addTrackToPlaylist(playlistId, track.id)
            }
            onComplete()
        }
    }

}
