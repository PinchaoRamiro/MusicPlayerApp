package com.example.musicplayerapp.data.mapper

import com.example.musicplayerapp.data.database.entities.MusicTrackEntity
import com.example.musicplayerapp.data.model.MusicTrack

fun MusicTrack.toDomain(): MusicTrackEntity {
    return MusicTrackEntity(
        trackId = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        duration = this.duration,
        data = this.data
    )
}
