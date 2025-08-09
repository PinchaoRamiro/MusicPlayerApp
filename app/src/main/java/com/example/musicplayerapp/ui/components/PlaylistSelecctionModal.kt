package com.example.musicplayerapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.data.database.entities.PlaylistWithCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSelectionModal(
    playlists: List<PlaylistWithCount>, // Lista de (id, nombre)
    onDismiss: () -> Unit,
    onPlaylistSelected: (Long) -> Unit
) {
    if (playlists.isEmpty()) return
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Selecciona una playlist", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            playlists.forEach { (id, name) ->
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onPlaylistSelected(id)
                        onDismiss()
                    }
                ) {
                    Text(name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
