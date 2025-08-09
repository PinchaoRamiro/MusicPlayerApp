package com.example.musicplayerapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.viewmodel.QueueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    viewModel: QueueViewModel = hiltViewModel()
) {
    val queue by viewModel.queue.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cola de reproducción") })
        }
    ) { padding ->
        ReorderableList(
            items = queue,
            onMove = { from, to ->
                viewModel.moveTrack(from, to)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun ReorderableList(
    items: List<MusicTrack>,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LazyColumn(modifier = modifier) {
        itemsIndexed(items, key = { _, track -> track.id }) { index, track ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(if (index == draggedIndex) Color.Gray else Color.Transparent)
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { draggedIndex = index },
                            onDragEnd = { draggedIndex = null },
                            onDragCancel = { draggedIndex = null },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetY += dragAmount.y
                                val targetIndex = (index + (offsetY / 100).toInt())
                                    .coerceIn(0, items.lastIndex)
                                if (targetIndex != index) {
                                    onMove(index, targetIndex)
                                    offsetY = 0f
                                }
                            }
                        )
                    }
                    .padding(16.dp)
            ) {
                ListItem(
                    headlineContent = { Text(track.title) },
                    supportingContent = { Text(track.artist) }
                )
            }
        }
    }
}
