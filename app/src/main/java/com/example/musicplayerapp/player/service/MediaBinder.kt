package com.example.musicplayerapp.player.service

import android.os.Binder

class MediaBinder(private val service: MediaPlaybackService) : Binder() {
    fun getService(): MediaPlaybackService = service
}
