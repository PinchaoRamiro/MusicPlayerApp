package com.example.musicplayerapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.ui.components.NowPlayingFooter
import com.example.musicplayerapp.ui.components.MusicListItem
import com.example.musicplayerapp.ui.components.ErrorContent
import com.example.musicplayerapp.ui.components.LoadingContent
import com.example.musicplayerapp.ui.theme.DarkColorScheme
import com.example.musicplayerapp.viewmodel.MusicListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListScreen(
    viewModel: MusicListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleModeEnabled.collectAsState()

    MaterialTheme(colorScheme = DarkColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My music") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),

                )
            },
            bottomBar = {
                NowPlayingFooter(
                    currentTrack = currentTrack,
                    isPlaying = isPlaying,
                    isShuffleEnabled = isShuffleEnabled,
                    onPlayPauseClick = {
                        if (isPlaying) viewModel.pauseTrack(viewModel.getApplication())
                        else currentTrack?.let { viewModel.playTrack(viewModel.getApplication(), it) }
                    },
                    onNextClick = { viewModel.nextTrack(viewModel.getApplication()) },
                    onPreviousClick = { viewModel.previousTrack(viewModel.getApplication()) },
                    onToggleShuffleClick = { viewModel.toggleShuffle(viewModel.getApplication()) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (uiState.isLoading) {
                    LoadingContent()
                } else if (uiState.error != null) {
                    ErrorContent(uiState.error!!)
                } else {
                    MusicListContent(
                        tracks = uiState.tracks,
                        onTrackClick = { track ->
                            viewModel.playTrack(viewModel.getApplication(), track)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MusicListContent(tracks: List<MusicTrack>, onTrackClick: (MusicTrack) -> Unit) {
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
            MusicListItem(track = track, onClick = { onTrackClick(track) })
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
        MusicTrack("7", "Venom", "Eminem", "Venom", 275000, ""),
    )
    MaterialTheme(colorScheme = DarkColorScheme) {
        MusicListContent(tracks = dummyTracks, onTrackClick = {})
    }
}