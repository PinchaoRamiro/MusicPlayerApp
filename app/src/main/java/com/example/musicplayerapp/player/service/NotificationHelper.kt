package com.example.musicplayerapp.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaSessionCompat
import com.example.musicplayerapp.MainActivity
import com.example.musicplayerapp.R
import com.example.musicplayerapp.player.controller.MusicPlayerController

class NotificationHelper(
    private val context: Context,
    private val playerController: MusicPlayerController
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            MediaPlaybackService.CHANNEL_ID,
            "Music Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Canal para control de reproducción de música"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun buildNotification(
        mediaSession: MediaSessionCompat,
        isPlaying: Boolean
    ): Notification {
        val openAppIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val previousIntent = createServiceIntent(MediaPlaybackService.ACTION_PREVIOUS)
        val playPauseIntent = createServiceIntent(
            if (isPlaying) MediaPlaybackService.ACTION_PAUSE
            else MediaPlaybackService.ACTION_PLAY
        )
        val nextIntent = createServiceIntent(MediaPlaybackService.ACTION_NEXT)

        val currentTitle = playerController.getCurrentTitle()?.takeIf { it.isNotBlank() }
            ?: "Reproduciendo música"
        val currentArtist = playerController.getCurrentArtist()?.takeIf { it.isNotBlank() }
            ?: "Artista desconocido"

        return NotificationCompat.Builder(context, MediaPlaybackService.CHANNEL_ID)
            .setContentTitle(currentTitle)
            .setContentText(currentArtist)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(openAppIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(android.R.drawable.ic_media_previous, "Anterior", previousIntent)
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause
                else android.R.drawable.ic_media_play,
                if (isPlaying) "Pausa" else "Reproducir",
                playPauseIntent
            )
            .addAction(android.R.drawable.ic_media_next, "Siguiente", nextIntent)
            .build()
    }

    private fun createServiceIntent(action: String): PendingIntent {
        val intent = Intent(context, MediaPlaybackService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}