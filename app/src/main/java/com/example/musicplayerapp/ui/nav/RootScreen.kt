package com.example.musicplayerapp.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.ui.theme.DarkColorScheme
import com.example.musicplayerapp.viewmodel.MusicListViewModel
import com.example.musicplayerapp.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootScreen(
    musicListViewModel: MusicListViewModel,
    playlistViewModel: PlaylistViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            GlobalTopBar(
                navController = navController,
                currentRoute = currentRoute,
                playlistViewModel = playlistViewModel
            )
        }
    ) { paddingValues ->
        MusicNavGraph(
            navController = navController,
            musicListViewModel = musicListViewModel,
            playlistViewModel = playlistViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalTopBar(
    navController: NavHostController,
    currentRoute: String?,
    playlistViewModel: PlaylistViewModel
) {
    MaterialTheme(colorScheme = DarkColorScheme) {
        TopAppBar(
            title = {
                Text(
                    text = getTitleForRoute(currentRoute, navController, playlistViewModel),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (navController.previousBackStackEntry != null) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            },
            actions = {
                if (currentRoute == MusicNavDestinations.MUSIC_LIST_ROUTE) {
                    IconButton(onClick = { navController.navigate(MusicNavDestinations.PLAYLISTS_ROUTE) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Ver playlists"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

@Composable
fun getTitleForRoute(
    currentRoute: String?,
    navController: NavHostController,
    playlistViewModel: PlaylistViewModel
): String {
    return when (currentRoute) {
        MusicNavDestinations.MUSIC_LIST_ROUTE -> "All music"
        MusicNavDestinations.PLAYLISTS_ROUTE -> "My Playlist"
        else -> {
            // Lógica para obtener el nombre de la playlist si estamos en su pantalla de detalles
            val playlistId = navController.currentBackStackEntry
                ?.arguments?.getString("playlistId")?.toLong()

            val allPlaylists by playlistViewModel.uiState.collectAsState()
            val playlist = allPlaylists.playlists.firstOrNull { it.playlistId == playlistId }

            playlist?.name ?: "Detalles de Playlist"
        }
    }
}