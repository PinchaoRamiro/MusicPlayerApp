package com.example.musicplayerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayerapp.ui.components.MusicListItem
import com.example.musicplayerapp.viewmodel.FavoritesViewModel
import com.example.musicplayerapp.player.service.MusicServiceConnection

@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel
) {
    val favoriteTracks = favoritesViewModel.uiState.collectAsState().value.favorites

    if (favoriteTracks.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes canciones favoritas aún.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            Log.d("FavoritesScreen", "Favorite tracks: $favoriteTracks")
            items(favoriteTracks) { track ->
                MusicListItem(
                    track = track,
                    onClick = {
                        favoritesViewModel.setPlaylist(favoriteTracks, favoriteTracks.indexOf(track), 0)
                        favoritesViewModel.playTrack(track)
                    },
                    showMenu = true,
                    onMenuClick = {
                        favoritesViewModel.removeFavorite(track.id)
                    }
                )
            }
        }
    }
}
