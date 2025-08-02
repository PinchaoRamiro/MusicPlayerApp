package com.example.musicplayerapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicplayerapp.ui.nav.MusicNavDestinations.ADD_TO_PLAYLIST_ROUTE
import com.example.musicplayerapp.ui.screen.AddToPlaylistScreen
import com.example.musicplayerapp.ui.screen.PlaylistDetailScreen
import com.example.musicplayerapp.ui.screen.MusicListScreen
import com.example.musicplayerapp.ui.screen.PlaylistsScreen
import com.example.musicplayerapp.viewmodel.MusicListViewModel
import com.example.musicplayerapp.viewmodel.PlaylistViewModel

// Define las rutas de navegación
object MusicNavDestinations {
    const val MUSIC_LIST_ROUTE = "music_list"
    const val PLAYLISTS_ROUTE = "playlists"
    const val PLAYLIST_DETAIL_ROUTE = "playlist_detail"
    const val ADD_TO_PLAYLIST_ROUTE = "add_to_playlist"

}

@Composable
fun MusicNavGraph(
    navController: NavHostController,
    musicListViewModel: MusicListViewModel,
    playlistViewModel: PlaylistViewModel, // Añadimos el PlaylistViewModel
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MusicNavDestinations.MUSIC_LIST_ROUTE,
        modifier = modifier
    ) {
        // Pantalla de la lista de música
        composable(route = MusicNavDestinations.MUSIC_LIST_ROUTE) {
            MusicListScreen(
                viewModel = musicListViewModel,
            )
        }

        // Pantalla de todas las playlists
        composable(route = MusicNavDestinations.PLAYLISTS_ROUTE) {
            PlaylistsScreen(
                viewModel = playlistViewModel, // Pasamos el PlaylistViewModel
                onPlaylistClick = { playlist ->
                    // Navegamos a la pantalla de detalles de la playlist
                    navController.navigate("${MusicNavDestinations.PLAYLIST_DETAIL_ROUTE}/${playlist.playlistId}")
                }
            )
        }

        // Pantalla de detalles de una playlist
        composable(
            route = "${MusicNavDestinations.PLAYLIST_DETAIL_ROUTE}/{playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")
            if (playlistId != null) {
                PlaylistDetailScreen(
                    playlistId = playlistId.toLong(),
                    musicListViewModel = musicListViewModel,
                    playlistViewModel = playlistViewModel,
                    navController = navController
                )
            }
        }

        composable(
            route = "$ADD_TO_PLAYLIST_ROUTE/{playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: return@composable
            AddToPlaylistScreen(
                playlistId = playlistId,
                onBack = { navController.popBackStack() }
            )
        }

    }
}