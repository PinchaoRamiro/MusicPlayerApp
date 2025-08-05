package com.example.musicplayerapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.utils.extractAlbumArt

@Composable
fun SongInfoScreen(
    track: MusicTrack?,
    isPlaying: Boolean,
    currentPosition: Long,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleFavorite: () -> Unit,
    isFavorite: Boolean,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val albumArt = remember(track?.data) {
            track?.data?.let { extractAlbumArt(it) }
        }

        if (albumArt != null) {
            Image(
                bitmap = albumArt.asImageBitmap(),
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }else{
            Image(
                painter = painterResource(id = R.drawable.ic_music_note),
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(250.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        if (track == null) {
            Text("No hay canción seleccionada", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            return
        }

        // Info de la canción
        Text(track.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Text(track.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(16.dp))

        // Controles superiores
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
            IconButton(onClick = { /* TODO: Agregar a playlist */ }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add to Playlist", tint = Color.White)
            }
            IconButton(onClick = { /* TODO: Temporizador */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Timer", tint = Color.White)
            }
            IconButton(onClick = { /* TODO: Volumen */ }) {
                Icon(Icons.Default.Share, contentDescription = "Volume", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progreso
        if (track.duration > 0) {
            Slider(
                enabled = track.data.isNotEmpty(),
                value = currentPosition.coerceIn(0L, track.duration).toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..track.duration.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()

            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(if(track.duration < 0) 0 else currentPosition), color = MaterialTheme.colorScheme.onSurface)
            Text(formatTime(track.duration), color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Controles principales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Shuffle */ }) {
                Icon(painter = painterResource(id = R.drawable.shuffle_24px), contentDescription = "Shuffle", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onPrevious) {
                Icon(painter = painterResource(id = R.drawable.skip_previous), contentDescription = "Previous", tint = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(64.dp)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            ) {
                Icon(
                    painter = if (isPlaying) painterResource(id = R.drawable.pause) else painterResource(id = R.drawable.play_arrow),
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = onNext) {
                Icon(painter = painterResource(id = R.drawable.skip_next), contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = { /* TODO: Cola */ }) {
                Icon(painter = painterResource(id = R.drawable.queue_music), contentDescription = "Queue", tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val minutes = millis / 60000
    val seconds = (millis % 60000) / 1000
    return "%02d:%02d".format(minutes, seconds)
}
