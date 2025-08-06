package com.example.musicplayerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.ui.components.MusicListItem
import com.example.musicplayerapp.ui.components.ErrorContent
import com.example.musicplayerapp.ui.components.LoadingContent
import com.example.musicplayerapp.ui.components.NowPlayingFooter
import com.example.musicplayerapp.ui.components.PlaylistSelectionModal
import com.example.musicplayerapp.ui.components.TrackOptionsModal
import com.example.musicplayerapp.ui.theme.DarkColorScheme
import com.example.musicplayerapp.viewmodel.FavoritesViewModel
import com.example.musicplayerapp.viewmodel.MusicListViewModel
import com.example.musicplayerapp.viewmodel.MusicServiceConnection
import com.example.musicplayerapp.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListScreen(
    viewModel: MusicListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MaterialTheme(colorScheme = DarkColorScheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> LoadingContent()
                uiState.error != null -> ErrorContent(uiState.error!!)
                else -> MusicListContent(
                    tracks = uiState.tracks,
                    onTrackClick = {  track ->
                        viewModel.setPlaylist(uiState.tracks, uiState.tracks.indexOf(track), -1)
                        viewModel.playTrack(track)
                    },
                    musicServiceConnection = viewModel.musicServiceConnection

                )
            }
        }
    }
}

@Composable
fun MusicListContent(
    tracks: List<MusicTrack>,
    onTrackClick: (MusicTrack) -> Unit,
    viewModel: MusicListViewModel = hiltViewModel(),
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    musicServiceConnection: MusicServiceConnection
) {
    var selectedTrack by remember { mutableStateOf<MusicTrack?>(null) }
    var isSheetOpen by remember { mutableStateOf(false) }
    var currentPlaylistId = viewModel.playlistId.collectAsState().value
    var showPlaylistModal by remember { mutableStateOf(false) }
    val allPlaylists = playlistViewModel.uiState.collectAsState().value.playlists


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stickyHeader {
            Row {
                Text(
                    text = "${tracks.size} songs",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp, horizontal = 0.dp)
                )
            }
        }
        items(tracks) { track ->
            MusicListItem(
                track = track,
                onClick = { onTrackClick(track) },
                showMenu = true,
                onMenuClick = {
                    selectedTrack = track
                    isSheetOpen = true
                }
            )
        }
    }
    if (isSheetOpen) {
        TrackOptionsModal(
            track = selectedTrack!!,
            onDismiss = { isSheetOpen = false },
            onAddToFavorites = {
                Log.d("MusicListScreen", "Añadir a favoritos: ${selectedTrack!!.title}")
                favoritesViewModel.toggleFavorite(trackId = selectedTrack!!.id)
                isSheetOpen = false
            },
            onAddToPlaylist = {
                Log.d("MusicListScreen", "Añadir a playlist: ${selectedTrack!!.title}")
                showPlaylistModal = true
            },

            onRemoveFromPlaylist = {
                Log.d("MusicListScreen", "Eliminar de playlist: ${selectedTrack!!.title}")
                playlistViewModel.removeTrackFromPlaylist( currentPlaylistId ?: -1, selectedTrack!!.id)
                isSheetOpen = false
                },
            onPlayNext = {
                Log.d("MusicListScreen", "Reproducir siguiente: ${selectedTrack!!.title}")
                musicServiceConnection.queueNext(selectedTrack!!.id)
                isSheetOpen = false
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

@Preview(showBackground = true)
@Composable
fun MusicListScreenPreview() {
    val dummyTracks = listOf(
        MusicTrack("1", "Royal Flush", "Harry Mack", "None", 275000, ""),
        MusicTrack("2", "Last One Standing", "Skylar Grey, Polo G, Mozzy, Eminem", "Venom", 275000, ""),
        MusicTrack("3", "We Don't Talk Any More", "Charlie Puth", "Nine Track Mind", 275000, ""),
        MusicTrack("4", "Blinding Lights", "The Weeknd", "After Hours", 275000, ""),
        MusicTrack("5", "Enemy", "Imagine Dragons, J.I.D", "Arcane", 275000, ""),
        MusicTrack("6", "Old Town Road", "Lil Nas X, Billy Ray Cyrus", "7", 275000, ""),
        MusicTrack("7", "Venom", "Eminem", "Venom", 275000, "")
    )
    MaterialTheme(colorScheme = DarkColorScheme) {
        MusicListContent(
            tracks = dummyTracks, onTrackClick = {},
            viewModel = TODO(),
            playlistViewModel = TODO(),
            musicServiceConnection = TODO()
        )
    }
}
