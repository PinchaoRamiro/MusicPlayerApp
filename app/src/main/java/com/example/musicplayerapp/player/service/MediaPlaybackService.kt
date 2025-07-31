package com.example.musicplayerapp.player.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.browse.MediaBrowser.MediaItem
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.player.controller.MusicPlayerController
import com.example.musicplayerapp.player.session.MediaSessionManager
import java.util.ArrayList

class MediaPlaybackService : Service() {

    private lateinit var mediaBinder: MediaBinder
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var playerController: MusicPlayerController

    private var trackList: List<MusicTrack> = emptyList()

    override fun onCreate() {
        super.onCreate()
        Log.d("MediaPlaybackService", "MediaPlaybackService created.")

        mediaBinder = MediaBinder(this)

        playerController = MusicPlayerController(this)
        notificationHelper = NotificationHelper(this, playerController)
        mediaSessionManager = MediaSessionManager(this)

        notificationHelper.createNotificationChannel()

        playerController.exoPlayer.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                updateNotificationAndPlaybackState()
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                updateNotificationAndPlaybackState()
            }

            override fun onMediaItemTransition(
                mediaItem: androidx.media3.common.MediaItem?,
                reason: Int
            ) {
                updateNotificationAndPlaybackState()
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder = mediaBinder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("MediaPlaybackService", "onStartCommand received action: $action, startId: $startId")

        when (action) {
            ACTION_PLAY -> {
                Log.d("MediaPlaybackService", "Received ACTION_PLAY intent, attempting playback.")
                val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_TRACK, MusicTrack::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_TRACK)
                }
                val playlist = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableArrayListExtra(EXTRA_PLAYLIST, MusicTrack::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableArrayListExtra(EXTRA_PLAYLIST)
                }
                val index = intent.getIntExtra(EXTRA_INDEX, 0)

                if (playlist != null && playlist.isNotEmpty()) {
                    Log.d("MediaPlaybackService", "ACTION_PLAY with playlist. Setting playlist with index $index.")
                    playerController.setPlaylist(playlist, index)
                    trackList = playlist
                    playerController.play(playlist[index])
                } else if (track != null) {
                    Log.d("MediaPlaybackService", "ACTION_PLAY with single track: ${track.title}.")
                    playerController.play(track)
                } else {
                    if (playerController.getCurrentTrack() != null && !playerController.isPlaying()) {
                        Log.d("MediaPlaybackService", "ACTION_PLAY received without new track/playlist. Resuming playback.")
                        playerController.resume()
                    } else if (playerController.getCurrentTrack() == null && trackList.isNotEmpty()){
                        Log.d("MediaPlaybackService", "ACTION_PLAY received but no current track. Playing first in playlist.")
                        playerController.play(trackList[0])
                    } else {
                        Log.d("MediaPlaybackService", "ACTION_PLAY received but nothing to play.")
                    }
                }
                updateNotificationAndPlaybackState()
            }
            ACTION_PAUSE -> {
                Log.d("MediaPlaybackService", "Received ACTION_PAUSE intent.")
                playerController.pause()
                updateNotificationAndPlaybackState()
            }
            ACTION_PREVIOUS -> {
                Log.d("MediaPlaybackService", "Received ACTION_PREVIOUS intent.")
                playerController.previous()
                updateNotificationAndPlaybackState()
            }
            ACTION_NEXT -> {
                Log.d("MediaPlaybackService", "Received ACTION_NEXT intent.")
                playerController.next()
                updateNotificationAndPlaybackState()
            }
            ACTION_STOP -> {
                Log.d("MediaPlaybackService", "Received ACTION_STOP intent. Stopping service.")
                stopSelf()
            }
            ACTION_SET_PLAYLIST_ONLY -> {
                Log.d("MediaPlaybackService", "Received ACTION_SET_PLAYLIST_ONLY intent. Setting playlist without auto-play.")
                val playlist = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableArrayListExtra(EXTRA_PLAYLIST, MusicTrack::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableArrayListExtra(EXTRA_PLAYLIST)
                }
                val index = intent.getIntExtra(EXTRA_INDEX, 0)

                if (playlist != null && playlist.isNotEmpty()) {
                    playerController.setPlaylist(playlist, index, autoPlay = false)
                    trackList = playlist
                    Log.d("MediaPlaybackService", "Playlist set to ${playlist.size} tracks, starting at index $index.")
                } else {
                    Log.w("MediaPlaybackService", "ACTION_SET_PLAYLIST_ONLY received but playlist is null or empty.")
                }
            }
            ACTION_TOGGLE_SHUFFLE -> {
                playerController.toggleShuffleMode()
                Log.d("MediaPlaybackService", "Shuffle mode toggled. Current state: ${playerController.isShuffleModeEnabled.value}")
            }
            else -> {
                Log.d("MediaPlaybackService", "Service started with null or unrecognized action: $action. Checking current state.")
            }
        }
        return START_STICKY
    }

    private fun updateNotificationAndPlaybackState() {
        val isPlaying = playerController.isPlaying()
        val currentPosition = playerController.exoPlayer.currentPosition

        val playbackState = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED

        Log.d("MediaPlaybackService", "Updating state: isPlaying=$isPlaying, PlaybackState=$playbackState, Position=$currentPosition")

        mediaSessionManager.updatePlaybackState(playbackState, currentPosition)

        val notification = notificationHelper.buildNotification(
            mediaSessionManager.getSession(),
            isPlaying
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MediaPlaybackService", "MediaPlaybackService destroyed. Releasing resources.")
        mediaSessionManager.release()
        playerController.release()
        stopForeground(true)
    }

    companion object {
        const val CHANNEL_ID = "music_channel"
        const val NOTIFICATION_ID = 101

        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_SET_PLAYLIST_ONLY = "ACTION_SET_PLAYLIST_ONLY"
        const val ACTION_TOGGLE_SHUFFLE = "ACTION_TOGGLE_SHUFFLE"

        private const val EXTRA_TRACK = "extra_track"
        private const val EXTRA_PLAYLIST = "extra_playlist"
        private const val EXTRA_INDEX = "extra_index"

        fun play(context: Context, track: MusicTrack) {
            val intent = Intent(context, MediaPlaybackService::class.java)
                .setAction(ACTION_PLAY)
                .putExtra(EXTRA_TRACK, track)
            context.startService(intent)
            Log.d("MediaPlaybackService", "Sent ACTION_PLAY intent for single track: ${track.title}")
        }

        fun pause(context: Context) {
            val intent = Intent(context, MediaPlaybackService::class.java)
                .setAction(ACTION_PAUSE)
            context.startService(intent)
            Log.d("MediaPlaybackService", "Sent ACTION_PAUSE intent.")
        }

        fun next(context: Context) {
            val intent = Intent(context, MediaPlaybackService::class.java)
                .setAction(ACTION_NEXT)
            context.startService(intent)
            Log.d("MediaPlaybackService", "Sent ACTION_NEXT intent.")
        }

        fun previous(context: Context) {
            val intent = Intent(context, MediaPlaybackService::class.java)
                .setAction(ACTION_PREVIOUS)
            context.startService(intent)
            Log.d("MediaPlaybackService", "Sent ACTION_PREVIOUS intent.")
        }

        fun setPlaylistOnly(context: Context, tracks: List<MusicTrack>, startIndex: Int = 0) {
            val intent = Intent(context, MediaPlaybackService::class.java)
                .setAction(ACTION_SET_PLAYLIST_ONLY)
                .putParcelableArrayListExtra(EXTRA_PLAYLIST, ArrayList(tracks))
                .putExtra(EXTRA_INDEX, startIndex)
            context.startService(intent)
            Log.d("MediaPlaybackService", "Sent ACTION_SET_PLAYLIST_ONLY intent (size: ${tracks.size}, start index: $startIndex).")
        }

        fun toggleShuffle(context: Context) {
            val intent = Intent(context, MediaPlaybackService::class.java).apply {
                action = ACTION_TOGGLE_SHUFFLE
            }
            context.startService(intent)
        }
    }
}