package com.example.musicplayerapp.viewmodel

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.annotation.Nullable
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayerapp.data.model.MusicTrack
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _currentTrack = MutableStateFlow<MusicTrack?>(null)
    val currentTrack: StateFlow<MusicTrack?> = _currentTrack.asStateFlow()

    private var allTracks = mutableListOf<MusicTrack>()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private var controller: MediaController? = null

    fun connect() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, com.example.musicplayerapp.player.service.PlaybackService::class.java)
        )

        val futureController = MediaController.Builder(context, sessionToken).buildAsync()

        futureController.addListener({
            val mediaController = futureController.get()
            controller = mediaController

            mediaController.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    _currentTrack.value = mediaItem?.let { item ->
                        MusicTrack(
                            id = item.mediaId,
                            title = item.mediaMetadata.title.toString(),
                            artist = item.mediaMetadata.artist.toString(),
                            album = item.mediaMetadata.albumTitle.toString(),
                            duration = item.mediaMetadata.extras?.getLong("duration") ?: 0L,
                            data = item.localConfiguration?.uri.toString()
                        )
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    _isShuffleEnabled.value = shuffleModeEnabled
                }
            })
        }, Runnable::run)
    }

    fun disconnect() {
        controller?.release()
        controller = null
    }

    fun play(track: MusicTrack) {
        Log.d("MusicServiceConnection", "Music play: $track")
        controller?.setMediaItem(
            MediaItem.Builder()
                .setUri(track.data.toUri())
                .setMediaId(track.id)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .build()
                )
                .build()
        )
        controller?.prepare()
        controller?.play()
    }

    fun pause() = controller?.pause()
    fun next() {
        controller?.seekToNext()/*
        val theNext: Int? = controller?.nextMediaItemIndex
        var song: MusicTrack? = null
        if(theNext != null){
            song = allTracks[theNext]
        }*/
    }
    fun previous() = controller?.seekToPrevious()

    fun toggleShuffle() {
        controller?.shuffleModeEnabled = !_isShuffleEnabled.value
    }

    fun setPlaylist(tracks: List<MusicTrack>, startIndex: Int) {
        Log.d("MusicServiceConnection", "Setting playlist with ${tracks.toString()} tracks")
        val mediaItems = tracks.map { track ->
            MediaItem.Builder()
                .setUri(track.data.toUri())
                .setMediaId(track.id)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .build()
                )
                .build()
        }
        allTracks = tracks as MutableList<MusicTrack>
        controller?.setMediaItems(mediaItems, startIndex, 0L)
        controller?.prepare()
        controller?.play()
    }
}
