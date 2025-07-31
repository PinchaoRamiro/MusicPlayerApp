package com.example.musicplayerapp.player.session

import android.content.Context
import android.support.v4.media.MediaMetadataCompat // Importar MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.musicplayerapp.player.service.MediaPlaybackService
import android.content.Intent

class MediaSessionManager(private val context: Context) {

    private val mediaSession: MediaSessionCompat

    init {
        mediaSession = MediaSessionCompat(context, "MusicService").apply {

            isActive = true

            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    val intent = Intent(context, MediaPlaybackService::class.java).apply {
                        action = MediaPlaybackService.ACTION_PLAY
                    }
                    context.startService(intent)
                }

                override fun onPause() {
                    super.onPause()
                    val intent = Intent(context, MediaPlaybackService::class.java).apply {
                        action = MediaPlaybackService.ACTION_PAUSE
                    }
                    context.startService(intent)
                }

                override fun onSkipToNext() {
                    super.onSkipToNext()
                    val intent = Intent(context, MediaPlaybackService::class.java).apply {
                        action = MediaPlaybackService.ACTION_NEXT
                    }
                    context.startService(intent)
                }

                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    val intent = Intent(context, MediaPlaybackService::class.java).apply {
                        action = MediaPlaybackService.ACTION_PREVIOUS
                    }
                    context.startService(intent)
                }
            })
        }
    }

    fun getSession(): MediaSessionCompat = mediaSession

    // Función para actualizar el estado de reproducción (Play/Pause, posición, velocidad)
    fun updatePlaybackState(state: Int, position: Long) {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(state, position, 1.0f)
        mediaSession.setPlaybackState(playbackStateBuilder.build())
    }

    // Función para actualizar los metadatos de la canción (Título, Artista, Álbum, etc.)
    fun updateMetadata(metadata: MediaMetadataCompat) {
        mediaSession.setMetadata(metadata)
    }

    fun release() {
        mediaSession.isActive = false
        mediaSession.release()
    }
}