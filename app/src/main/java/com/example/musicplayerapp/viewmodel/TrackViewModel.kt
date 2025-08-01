package com.example.musicplayerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.repository.MusicRepository as TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val repository: TrackRepository
) : ViewModel() {

    val tracks = repository.getAllTracks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun refreshMusicLibrary() {
        viewModelScope.launch {
            repository.refreshTracks()
        }
    }
}
