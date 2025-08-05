package com.example.musicplayerapp.viewmodel

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
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
import androidx.media3.common.MediaMetadata
import com.example.musicplayerapp.player.service.PlaybackService

@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _currentTrack = MutableStateFlow<MusicTrack?>(null)
    val currentTrack: StateFlow<MusicTrack?> = _currentTrack.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private var allTracks = mutableListOf<MusicTrack>()
    var playlistidRec = MutableStateFlow<Long?>(null)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private var controller: MediaController? = null

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun queueNext(trackId: String) {
        val track = allTracks.find { it.id == trackId } ?: return
        val mediaItem = track.toMediaItem()

        val currentIndex = controller?.currentMediaItemIndex ?: return
        val isShuffle = controller?.shuffleModeEnabled ?: false

        if (isShuffle) {
            // Si hay shuffle, se fuerza el modo ordenado temporalmente
            controller?.shuffleModeEnabled = false
        }

        // Insertar el track justo después del actual
        val insertIndex = currentIndex + 1
        controller?.addMediaItem(insertIndex, mediaItem)

        // También actualizamos la lista local para mantenerla sincronizada
        allTracks.add(insertIndex, track)

        if (isShuffle) {
            // Restaurar el modo shuffle
            controller?.shuffleModeEnabled = true
        }
    }


    fun getController(): MediaController? = controller

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

    fun connect() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context,PlaybackService::class.java)
        )

        val futureController = MediaController.Builder(context, sessionToken).buildAsync()

        futureController.addListener({
            var mediaController : MediaController?
            try {
                mediaController = futureController.get()
                controller = mediaController
            }
            catch (e: Exception) {
                return@addListener
            }


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
                    _currentPosition.value = mediaController.currentPosition
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
        // Buscar si la canción ya está en la playlist
        val index = allTracks.indexOfFirst { it.id == track.id }

        if (index != -1 && controller != null) {
            // Si existe → saltar a esa posición
            controller?.seekTo(index, 0L)
            controller?.play()
        } else {
            // Si no existe → reproducir como canción individual
            controller?.setMediaItem(
                track.toMediaItem()
            )
            controller?.prepare()
            controller?.play()
        }
    }


    fun pause() = controller?.pause()
    fun next() {
        controller?.seekToNext()
    }
    fun previous() = controller?.seekToPrevious()

    fun toggleShuffle() {
        controller?.shuffleModeEnabled = !_isShuffleEnabled.value
    }

    fun setPlaylist(tracks: List<MusicTrack>, startIndex: Int, playlistId: Long) {
        if(allTracks == tracks){
            return
        }
        playlistidRec = MutableStateFlow(playlistId)

        val mediaItems = tracks.map { track ->
            track.toMediaItem()
        }
        allTracks = tracks.toMutableList()
        controller?.setMediaItems(mediaItems, startIndex, 0L)
        controller?.prepare()
        _currentTrack.value = getCurrentMetadata()
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
                    /*.setArtworkUri(this.data.toUri())*/
                    .setExtras(Bundle().apply { putLong("duration", this@toMediaItem.duration) })
                    .build()
            ).build()
    }
}
