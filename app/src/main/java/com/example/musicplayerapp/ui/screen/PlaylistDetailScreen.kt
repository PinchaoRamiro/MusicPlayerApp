package com.example.musicplayerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.ui.components.MusicListItem
import com.example.musicplayerapp.ui.nav.MusicNavDestinations
import com.example.musicplayerapp.ui.theme.DarkColorScheme
import com.example.musicplayerapp.viewmodel.MusicListViewModel
import com.example.musicplayerapp.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    navController: NavController, // ⬅️ añadimos para navegar
    musicListViewModel: MusicListViewModel,
    playlistViewModel: PlaylistViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tracksState by playlistViewModel.getPlaylistTracks(playlistId).collectAsState()

    MaterialTheme(colorScheme = DarkColorScheme) {
        Log.d("PlaylistDetailScreen", "Playlist ID: $playlistId")
        Log.d("PlaylistDetailScreen", "Items: $tracksState")
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("${MusicNavDestinations.ADD_TO_PLAYLIST_ROUTE}/$playlistId")
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar canciones")
                }
            }
        ) { padding ->
            when {
                tracksState.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Esta playlist está vacía",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tracksState) { track: MusicTrack ->
                            MusicListItem(
                                track = track,
                                onClick = {
                                    musicListViewModel.playTrack(context = context, track = track)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
