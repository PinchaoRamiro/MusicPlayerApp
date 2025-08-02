package com.example.musicplayerapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayerapp.ui.components.MusicListItem
import com.example.musicplayerapp.viewmodel.AddToPlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistScreen(
    playlistId: Long,
    onBack: () -> Unit,
    viewModel: AddToPlaylistViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTracks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir canciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.addSelectedTracksToPlaylist(playlistId, onComplete = onBack)
                        },
                        enabled = state.selectedTrackIds.isNotEmpty()
                    ) {
                        Text("Guardar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(state.tracks) { track ->
                        MusicListItem(
                            track = track,
                            onClick = { viewModel.toggleSelection(track.id) },
                            showCheckbox = true,
                            checked = state.selectedTrackIds.contains(track.id)
                        )
                    }
                }
            }
        }
    }
}
