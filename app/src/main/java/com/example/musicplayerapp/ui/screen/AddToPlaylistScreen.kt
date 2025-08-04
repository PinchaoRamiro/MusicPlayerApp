package com.example.musicplayerapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayerapp.ui.components.LoadingContent
import com.example.musicplayerapp.ui.components.MusicListItem
import com.example.musicplayerapp.ui.theme.DarkColorScheme
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
    MaterialTheme(colorScheme = DarkColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Añadir canciones") },
                    actions = {
                        Button(
                            onClick = {
                                viewModel.addSelectedTracksToPlaylist(
                                    playlistId,
                                    onComplete = onBack
                                )
                            },
                            enabled = state.selectedTrackIds.isNotEmpty()
                        ) {
                            Text("Guardar")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                if (state.isLoading) {
                    LoadingContent()
                } else {
                    LazyColumn {
                        items(state.tracks) { track ->
                            MusicListItem(
                                track = track,
                                onClick = { viewModel.toggleSelection(track.id) },
                                showCheckbox = true,
                                checked = state.selectedTrackIds.contains(track.id),
                                onCheckedChange = {viewModel.toggleSelection(track.id)}
                            )
                        }
                    }
                }
            }
        }
    }
}
