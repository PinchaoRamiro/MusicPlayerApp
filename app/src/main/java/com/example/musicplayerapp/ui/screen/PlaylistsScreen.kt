package com.example.musicplayerapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayerapp.data.database.entities.PlaylistWithCount
import com.example.musicplayerapp.ui.components.PlaylistItem
import com.example.musicplayerapp.ui.theme.DarkColorScheme
import com.example.musicplayerapp.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    onPlaylistClick: (PlaylistWithCount) -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = DarkColorScheme) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreatePlaylistDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Crear nueva playlist"
                    )
                }
            }
        ) { paddingValues ->
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.playlists.isEmpty() -> {
                    EmptyPlaylistsContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.playlists) { playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                onClick = { onPlaylistClick(playlist) }
                            )
                        }
                    }
                }
            }
        }

        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onDismiss = { showCreatePlaylistDialog = false },
                onConfirm = { name ->
                    viewModel.createPlaylist(name)
                    showCreatePlaylistDialog = false
                }
            )
        }
    }
}

@Composable
fun EmptyPlaylistsContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Add),
                contentDescription = "No playlists found",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No has creado ninguna playlist",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = "Presiona '+' para crear una nueva",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    val isNameValid = playlistName.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear nueva playlist") },
        text = {
            Column {
                Text("Ingresa un nombre para tu nueva playlist.")
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Nombre de la playlist") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(playlistName) },
                enabled = isNameValid
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
