package com.example.musicplayerapp.viewmodel

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.player.service.PlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _currentTrack = MutableStateFlow<MusicTrack?>(null)
    val currentTrack: StateFlow<MusicTrack?> = _currentTrack.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration: StateFlow<Long> = _currentDuration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private var controller: MediaController? = null
    private var progressJob: Job? = null

    private var allTracks = mutableListOf<MusicTrack>()
    private var shuffledTracks = mutableListOf<MusicTrack>()
    private var shuffleIndex = 0
    private var isUsingCustomShuffle = false

    // id de la playlist
    var playlistRec = MutableStateFlow<Long?>(null)

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                controller?.let { ctrl ->
                    if (ctrl.isPlaying) {
                        _currentPosition.update { ctrl.currentPosition }
                        _currentDuration.update { ctrl.duration.coerceAtLeast(1L) }
                    }
                }
                delay(500)
            }
        }
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun queueNext(trackId: String) {

        val track = allTracks.find { it.id == trackId } ?: return
        val mediaItem = track.toMediaItem()
        val currentIndex = controller?.currentMediaItemIndex ?: return

        // Verifica si ya está como siguiente
        val nextItem = controller?.getMediaItemAt(currentIndex + 1)
        if (nextItem?.mediaId == track.id) {
            Log.d("MusicServiceConnection", "Track already next in queue.")
            return
        }
        // Elimina si ya está en la lista
        val existingIndex = shuffledTracks.indexOfFirst { it.id == track.id }
        if (existingIndex != -1) {
            controller?.removeMediaItem(existingIndex)
            Log.d("MusicServiceConnection", "Removed track from index $existingIndex")
        }
        // Inserta después del actual
        val insertIndex = currentIndex + 1
        controller?.addMediaItem(insertIndex, mediaItem)
        Log.d("MusicServiceConnection", "Inserted track '${track.title}' at index $insertIndex")
    }

    fun connect() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val futureController = MediaController.Builder(context, sessionToken).buildAsync()

        futureController.addListener({
            try {
                controller = futureController.get()
                startProgressUpdates()

                controller?.addListener(object : Player.Listener {

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        _currentTrack.value = mediaItem?.let {
                            MusicTrack(
                                id = it.mediaId,
                                title = it.mediaMetadata.title.toString(),
                                artist = it.mediaMetadata.artist.toString(),
                                album = it.mediaMetadata.albumTitle.toString(),
                                duration = it.mediaMetadata.extras?.getLong("duration") ?: 0L,
                                data = it.localConfiguration?.uri.toString()
                            )
                        }
                        _currentPosition.value = controller?.currentPosition ?: 0L
                        _currentDuration.value = controller?.duration ?: 0L
                        if (isUsingCustomShuffle) {
                            shuffleIndex = controller?.currentMediaItemIndex ?: 0
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        _isShuffleEnabled.value = shuffleModeEnabled
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            handleTrackEnd()
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("MusicServiceConnection", "Failed to connect to media controller", e)
            }
        }, Runnable::run)
    }

    fun disconnect() {
        progressJob?.cancel()
        controller?.release()
        controller = null
    }

    fun play(track: MusicTrack) {
        var index: Int? = null
        if(isUsingCustomShuffle){
            index = shuffledTracks.indexOf(track)
            shuffleIndex = shuffledTracks.indexOf(track)
        }else
            index = allTracks.indexOf(track)

        if (index != -1) {
            controller?.seekTo(index, 0)
            controller?.play()
        } else {
            controller?.setMediaItem(track.toMediaItem())
            controller?.prepare()
            controller?.play()
        }
    }

    fun pause() = controller?.pause()

    fun next() {
        if (isUsingCustomShuffle) {
            controller?.seekToNext()
            shuffleIndex = controller?.currentMediaItemIndex ?: 0
        } else {
            if (allTracks.indexOf(_currentTrack.value) >= allTracks.size - 1) {
                controller?.seekTo(0, 0L)
            } else {
                controller?.seekToNext()
            }
        }
    }

    fun previous() {
        if (isUsingCustomShuffle) {
            controller?.seekToPrevious()
            shuffleIndex = controller?.currentMediaItemIndex ?: 0
        } else {
            if (allTracks.indexOf(_currentTrack.value) == 0) {
                controller?.seekTo(allTracks.size - 1, 0L)
            } else {
                controller?.seekToPrevious()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun toggleShuffle() {
        isUsingCustomShuffle = !isUsingCustomShuffle
        _isShuffleEnabled.value = isUsingCustomShuffle

        if (isUsingCustomShuffle) {
            shuffledTracks = allTracks.shuffled().toMutableList()
            shuffledTracks.addFirst(_currentTrack.value!!)
            shuffleIndex = shuffledTracks.indexOf(_currentTrack.value) + 1
            controller?.setMediaItems(shuffledTracks.map { it.toMediaItem() }, shuffleIndex - 1, _currentPosition.value)
        }else{
            controller?.setMediaItems(allTracks.map { it.toMediaItem() }, allTracks.indexOf(_currentTrack.value), _currentPosition.value)
        }
    }

    fun setPlaylist(tracks: List<MusicTrack>, startIndex: Int, playlistId: Long) {
        if (allTracks == tracks) return

        playlistRec.value = playlistId
        allTracks = tracks.toMutableList()

        controller?.setMediaItems(tracks.map { it.toMediaItem() }, startIndex, 0L)
        controller?.prepare()

        _currentTrack.value = getCurrentMetadata()

        if (isUsingCustomShuffle) {
            shuffledTracks = tracks.shuffled().toMutableList()
            shuffleIndex = 0
        }
    }

    private fun handleTrackEnd() {
        if (isUsingCustomShuffle) {
            shuffleIndex++
            if (shuffleIndex >= shuffledTracks.size) shuffleIndex = 0
            play(shuffledTracks[shuffleIndex])
        } else {
            val nextIndex = (controller?.currentMediaItemIndex ?: 0) + 1
            if (nextIndex >= allTracks.size) {
                play(allTracks[0])
            } else {
                controller?.seekToNext()
            }
        }
    }

    fun getCurrentMetadata(): MusicTrack? {
        val item = controller?.currentMediaItem ?: return null
        return MusicTrack(
            id = item.mediaId,
            title = item.mediaMetadata.title.toString(),
            artist = item.mediaMetadata.artist.toString(),
            album = item.mediaMetadata.albumTitle.toString(),
            duration = item.mediaMetadata.extras?.getLong("duration") ?: 0L,
            data = item.localConfiguration?.uri.toString()
        )
    }

    private fun MusicTrack.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri(this.data.toUri())
            .setMediaId(this.id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(this.title)
                    .setArtist(this.artist)
                    .setAlbumTitle(this.album)
                    .setExtras(Bundle().apply {
                        putLong("duration", this@toMediaItem.duration)
                    })
                    .build()
            )
            .build()
    }
}