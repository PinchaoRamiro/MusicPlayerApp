package com.example.musicplayerapp.viewmodel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.player.service.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
) : ViewModel() {

    private val _queue = MutableStateFlow<List<MusicTrack>>(emptyList())
    val queue: StateFlow<List<MusicTrack>> = _queue

    val isShuffleModeEnabled: StateFlow<Boolean> = musicServiceConnection.isShuffleEnabled

    init {
        Log.d("QueueViewModel", "init called")
        loadQueue()
    }

    fun loadQueue() {
        Log.d("QueueViewModel", "Loading queue...")
        viewModelScope.launch {
            _queue.update { musicServiceConnection.getQueue() }
        }
        Log.d("QueueViewModel", "Queue loaded: ${_queue.value}")
    }

    fun moveTrack(fromIndex: Int, toIndex: Int) {
        Log.d("QueueViewModel", "Moving track from $fromIndex to $toIndex")
        musicServiceConnection.moveTrack(fromIndex, toIndex)
        loadQueue() // Refrescamos después del cambio
    }
}
