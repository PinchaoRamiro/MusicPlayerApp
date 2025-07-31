package com.example.musicplayerapp.data.repository

import com.example.musicplayerapp.data.local.MusicMediaStoreDataSource
import com.example.musicplayerapp.data.model.MusicTrack

class MusicRepository(
    private val dataSource: MusicMediaStoreDataSource
) {

    suspend fun fetchAllTracks(): List<MusicTrack> {
        return dataSource.getAllTracks()
    }
}
