package com.example.musicplayerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.data.model.MusicTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOptionsModal(
    track: MusicTrack,
    onDismiss: () -> Unit,
    onAddToFavorites: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onRemoveFromPlaylist: (() -> Unit)? = null,
    onPlayNext: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.background( MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
    ) {
        Column(modifier = Modifier.padding(10.dp).padding( horizontal = 16.dp)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            SheetOptionItem( text = "Reproducir como siguiente") {
                onPlayNext()
                onDismiss()
            }

            SheetOptionItem(
                text = "Añadir a favoritos",
            ) {
                onAddToFavorites()
                onDismiss()
            }
            SheetOptionItem( text = "Añadir a playlist") {
                onAddToPlaylist()
            }
            if(onRemoveFromPlaylist != null) {
                SheetOptionItem(text = "Eliminar de la playlist") {
                    onRemoveFromPlaylist()
                    onDismiss()
                }
            }
        }
    }
}

@Composable
fun SheetOptionItem( text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {/*
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )*/
        Text(text = text, style = MaterialTheme.typography.bodyLarge , modifier = Modifier
            .background( MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            .padding(12.dp)
        )
    }
}
