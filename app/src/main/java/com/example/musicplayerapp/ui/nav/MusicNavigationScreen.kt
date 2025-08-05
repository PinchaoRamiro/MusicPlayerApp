package com.example.musicplayerapp.ui.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayerapp.ui.screen.AddToPlaylistScreen
import com.example.musicplayerapp.ui.screen.MusicListScreen
import com.example.musicplayerapp.ui.screen.PlaylistDetailScreen
import com.example.musicplayerapp.ui.screen.PlaylistsScreen
import com.example.musicplayerapp.ui.theme.DarkColorScheme
import com.example.musicplayerapp.viewmodel.MusicListViewModel
import com.example.musicplayerapp.viewmodel.PlaylistViewModel

object MusicNavDestinations {
    const val MUSIC_LIST_ROUTE = "music_list"
    const val PLAYLISTS_ROUTE = "playlists"
    const val PLAYLIST_DETAIL_ROUTE = "playlist_detail"
    const val ADD_TO_PLAYLIST_ROUTE = "add_to_playlist"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicNavigationScreen(
    musicListViewModel: MusicListViewModel,
    playlistViewModel: PlaylistViewModel
) {
    val navController = rememberNavController()
    val tabs = listOf(
        "Canciones" to MusicNavDestinations.MUSIC_LIST_ROUTE,
        "Listas" to MusicNavDestinations.PLAYLISTS_ROUTE
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    MaterialTheme(colorScheme = DarkColorScheme) {
        Scaffold { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, pair ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                navController.navigate(pair.second) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            text = {
                                Text(
                                    text = pair.first,
                                    color = if (selectedTabIndex == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = MusicNavDestinations.MUSIC_LIST_ROUTE,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(route = MusicNavDestinations.MUSIC_LIST_ROUTE) {
                        MusicListScreen(viewModel = musicListViewModel)
                    }
                    composable(route = MusicNavDestinations.PLAYLISTS_ROUTE) {
                        PlaylistsScreen(
                            viewModel = playlistViewModel,
                            onPlaylistClick = { playlist ->
                                navController.navigate("${MusicNavDestinations.PLAYLIST_DETAIL_ROUTE}/${playlist.playlistId}")
                            }
                        )
                    }
                    composable(
                        route = "${MusicNavDestinations.PLAYLIST_DETAIL_ROUTE}/{playlistId}",
                        arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val playlistId =
                            backStackEntry.arguments?.getString("playlistId")?.toLongOrNull()
                        if (playlistId != null) {
                            PlaylistDetailScreen(
                                playlistId = playlistId,
                                musicListViewModel = musicListViewModel,
                                playlistViewModel = playlistViewModel,
                                navController = navController
                            )
                        }
                    }
                    composable(
                        route = "${MusicNavDestinations.ADD_TO_PLAYLIST_ROUTE}/{playlistId}",
                        arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val playlistId =
                            backStackEntry.arguments?.getLong("playlistId") ?: return@composable
                        AddToPlaylistScreen(
                            playlistId = playlistId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}