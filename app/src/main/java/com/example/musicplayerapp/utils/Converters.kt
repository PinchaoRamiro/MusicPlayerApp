package com.example.musicplayerapp.utils


fun formatDuration(durationMillis: Long): String {
    val minutes = (durationMillis / 1000) / 60
    val seconds = (durationMillis / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}