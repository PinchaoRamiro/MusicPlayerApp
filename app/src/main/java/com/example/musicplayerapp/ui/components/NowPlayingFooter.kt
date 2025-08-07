package com.example.musicplayerapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.utils.extractAlbumArt

import com.example.musicplayerapp.ui.nav.MusicNavDestinations

@Composable
fun NowPlayingFooter(
    currentTrack: MusicTrack?,
    isPlaying: Boolean,
    isShuffleEnabled: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onToggleShuffleClick: () -> Unit,
    navController: NavController
) {
    if (currentTrack == null) {
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = MaterialTheme.colorScheme.background,
        onClick = {
            navController.navigate(MusicNavDestinations.NOW_PLAYING_ROUTE)
        }
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val albumArt = extractAlbumArt(currentTrack.data)

                if (albumArt != null) {
                    androidx.compose.foundation.Image(
                        bitmap = albumArt.asImageBitmap(),
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .size(48.dp)
                    )
                }else{
                    Icon(
                        painter = painterResource(id = R.drawable.ic_music_note),
                        contentDescription = "Music icon",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentTrack.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleShuffleClick) {
                    Icon(
                        painter =  painterResource(id = R.drawable.shuffle_24px),
                        contentDescription = "Toggle Shuffle",
                        tint = if(isShuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onPreviousClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.skip_previous),
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = onPlayPauseClick) {
                    Icon(
                        painter = if (!isPlaying) painterResource(id = R.drawable.play_arrow) else painterResource(id = R.drawable.pause),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = onNextClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.skip_next),
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}