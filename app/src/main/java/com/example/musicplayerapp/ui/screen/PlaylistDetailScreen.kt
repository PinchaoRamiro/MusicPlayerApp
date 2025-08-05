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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.ui.components.LoadingContent
import com.example.musicplayerapp.ui.components.MusicListItem
import com.example.musicplayerapp.ui.components.NowPlayingFooter
import com.example.musicplayerapp.ui.components.PlaylistSelectionModal
import com.example.musicplayerapp.ui.components.TrackOptionsModal
import com.example.musicplayerapp.ui.nav.MusicNavDestinations
import com.example.musicplayerapp.ui.theme.DarkColorScheme
import com.example.musicplayerapp.viewmodel.FavoritesViewModel
import com.example.musicplayerapp.viewmodel.MusicListViewModel
import com.example.musicplayerapp.viewmodel.MusicServiceConnection
import com.example.musicplayerapp.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    navController: NavController,
    musicListViewModel: MusicListViewModel,
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    musicServiceConnection: MusicServiceConnection,
) {
    val tracksState by playlistViewModel.getPlaylistTracks(playlistId).collectAsState()
    val currentTrack by musicListViewModel.currentTrack.collectAsState()
    val uiState by playlistViewModel.uiState.collectAsState()

    var selectedTrack by remember { mutableStateOf<MusicTrack?>(null) }
    var showMenuModal by remember { mutableStateOf(false) }
    var showPlaylistModal by remember { mutableStateOf(false) }
    val allPlaylists = playlistViewModel.uiState.collectAsState().value.playlists

    MaterialTheme(colorScheme = DarkColorScheme) {
        Log.d("PlaylistDetailScreen", "Playlist ID: $playlistId")
        Log.d("PlaylistDetailScreen", "Items: $tracksState")
        Log.d("PlaylistDetailScreen", "Current track: $currentTrack")
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
                uiState.isLoading -> LoadingContent()
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
                                    musicListViewModel.setPlaylist(tracksState, tracksState.indexOf(track), playlistId)
                                    musicListViewModel.playTrack(track)
                                },
                                showMenu = true,
                                onMenuClick = {
                                    selectedTrack = track
                                    showMenuModal = true
                                }
                            )
                        }
                    }
                }
            }
        }
        if (showMenuModal) {
        TrackOptionsModal(
            track = selectedTrack!!,
            onDismiss = { showMenuModal = false },
            onAddToFavorites = {
                Log.d("MusicListScreen", "Añadir a favoritos: ${selectedTrack!!.title}")
                favoritesViewModel.toggleFavorite(trackId = selectedTrack!!.id)
                showMenuModal = false
            },
            onAddToPlaylist = {
                Log.d("MusicListScreen", "Añadir a playlist: ${selectedTrack!!.title}")
                showPlaylistModal = true
            },

            onRemoveFromPlaylist = {
                Log.d("MusicListScreen", "Eliminar de playlist: ${selectedTrack!!.title}")
                playlistViewModel.removeTrackFromPlaylist( playlistId, selectedTrack!!.id)
                showMenuModal = false
            },
            onPlayNext = {
                Log.d("MusicListScreen", "Reproducir siguiente: ${selectedTrack!!.title}")
                musicServiceConnection.queueNext(selectedTrack!!.id)
                showMenuModal = false
            }
        )
        if (showPlaylistModal && selectedTrack != null) {
            Log.d("MusicListScreen", "Playlist modal: ${selectedTrack!!.title}")
            PlaylistSelectionModal(
                playlists = allPlaylists.map { it.playlistId to it.name },
                onDismiss = { showPlaylistModal = false },
                onPlaylistSelected = { playlistId ->
                    playlistViewModel.addTrackToPlaylist(playlistId, selectedTrack!!)
                    showPlaylistModal = false
                }
            )
        }

    }
    }
}
