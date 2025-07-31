package com.example.musicplayerapp.player.controller

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayerapp.data.model.MusicTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayerController(context: Context) {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private var originalPlaylist: List<MusicTrack> = emptyList()
    private var currentActivePlaylist: List<MusicTrack> = emptyList()

    private val _isShuffleModeEnabled = MutableStateFlow(false)
    val isShuffleModeEnabled: StateFlow<Boolean> = _isShuffleModeEnabled.asStateFlow()

    init {
        exoPlayer.prepare()
    }

    fun setPlaylist(tracks: List<MusicTrack>, startIndex: Int = 0, autoPlay: Boolean = true) {
        originalPlaylist = tracks
        currentActivePlaylist = if (_isShuffleModeEnabled.value) {
            tracks.shuffled()
        } else {
            tracks
        }

        val mediaItems = tracks.map { track ->
            MediaItem.Builder()
                .setUri(track.data)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .build()
                )
                .build()
        }
        exoPlayer.setMediaItems(mediaItems)

        val actualStartIndex = currentActivePlaylist.indexOfFirst { it.id == originalPlaylist.getOrNull(startIndex)?.id }
        if (actualStartIndex != -1) {
            exoPlayer.seekTo(actualStartIndex, 0)
        } else if (currentActivePlaylist.isNotEmpty()) {
            exoPlayer.seekTo(0, 0)
        }
        exoPlayer.seekTo(startIndex, 0)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = autoPlay
    }

    fun toggleShuffleMode() {
        val newShuffleState = !_isShuffleModeEnabled.value
        _isShuffleModeEnabled.value = newShuffleState

        if (originalPlaylist.isNotEmpty()) {
            val currentTrack = getCurrentTrack()
            val currentIndex = exoPlayer.currentMediaItemIndex

            val newPlaylist = if (newShuffleState) {
                originalPlaylist.shuffled()
            } else {
                originalPlaylist
            }

            currentActivePlaylist = newPlaylist

            val mediaItems = currentActivePlaylist.map { track ->
                MediaItem.Builder()
                    .setUri(track.data)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(track.title)
                            .setArtist(track.artist)
                            .setAlbumTitle(track.album)
                            .build()
                    )
                    .build()
            }

            exoPlayer.setMediaItems(mediaItems)

            val newIndex = currentTrack?.let { currentActivePlaylist.indexOfFirst { track -> track.id == it.id } }
            if (newIndex != null && newIndex != -1) {
                exoPlayer.seekTo(newIndex, 0)
            } else if (currentActivePlaylist.isNotEmpty()) {
                exoPlayer.seekTo(0, 0)
            }
            // No llamar a prepare() de nuevo si ya está preparado, setMediaItems puede ser suficiente
            // pero si necesitas un re-prepare en ciertos casos, agrégalo.
            // exoPlayer.prepare() // Considera si es necesario aquí, setMediaItems suele ser suficiente para reconfigurar.
        }
    }

    fun play(track: MusicTrack) {
        val index = currentActivePlaylist.indexOfFirst { it.id == track.id }
        if (index != -1) {
            if (exoPlayer.currentMediaItemIndex != index) {
                exoPlayer.seekTo(index, 0)
            }
            exoPlayer.playWhenReady = true
        } else {
            val currentMediaItems = mutableListOf<MediaItem>()
            for (i in 0 until exoPlayer.mediaItemCount) {
                exoPlayer.getMediaItemAt(i).let { currentMediaItems.add(it) }
            }
            // Si el track no está en la playlist activa, agrégalo y reprodúcelo
            // Opcional: Depende de cómo quieras manejar pistas individuales que no están en la playlist actual.
            // Para simplicidad en shuffle, usualmente se actúa sobre la lista actual.
            // Si agregas una pista aquí, considera si debe ir a la original o solo a la activa.

            val newMediaItem = MediaItem.Builder()
                .setUri(track.data)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .build()
                )
                .build()

            exoPlayer.addMediaItem(newMediaItem)
            exoPlayer.seekTo(exoPlayer.mediaItemCount - 1, 0)
            exoPlayer.playWhenReady = true
        }
    }

    fun resume() {
        exoPlayer.playWhenReady = true
    }

    fun pause() {
        exoPlayer.playWhenReady = false
    }

    fun next() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        } else {
            exoPlayer.seekToDefaultPosition(0)
        }
    }

    fun previous() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        } else {
            if (currentActivePlaylist.isNotEmpty()) {
                exoPlayer.seekToDefaultPosition(currentActivePlaylist.lastIndex)
            }
        }
    }

    fun seekNext() {
        exoPlayer.seekToNextMediaItem()
        exoPlayer.playWhenReady = true
    }

    fun seekPrevious() {
        exoPlayer.seekToPreviousMediaItem()
        exoPlayer.playWhenReady = true
    }

    fun getNextTrack(): MusicTrack? {
        if (currentActivePlaylist.isEmpty() || exoPlayer.currentMediaItemIndex == C.INDEX_UNSET) return null

        val nextIndex = if (exoPlayer.currentMediaItemIndex < currentActivePlaylist.lastIndex)
            exoPlayer.currentMediaItemIndex + 1
        else
            0
        return currentActivePlaylist[nextIndex]
    }

    fun isPlaying(): Boolean {
        return exoPlayer.playWhenReady &&
                (exoPlayer.playbackState == Player.STATE_READY ||
                        exoPlayer.playbackState == Player.STATE_BUFFERING)
    }

    fun getCurrentTitle(): String? {
        return exoPlayer.currentMediaItem?.mediaMetadata?.title?.toString()
    }

    fun getCurrentArtist(): String? {
        return exoPlayer.currentMediaItem?.mediaMetadata?.artist?.toString()
    }

    fun getCurrentTrack(): MusicTrack? {
        return if (currentActivePlaylist.isNotEmpty() && exoPlayer.currentMediaItemIndex != C.INDEX_UNSET) {
            currentActivePlaylist[exoPlayer.currentMediaItemIndex]
        } else {
            null
        }
    }

    fun release() {
        exoPlayer.release()
    }

    private fun <T> List<T>.shuffled(): List<T> {
        val list = toMutableList()
        list.shuffle()
        return list
    }
}