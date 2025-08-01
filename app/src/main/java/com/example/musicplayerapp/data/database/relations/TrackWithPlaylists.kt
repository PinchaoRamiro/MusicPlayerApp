package com.example.musicplayerapp.data.database.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.musicplayerapp.data.database.entities.MusicTrackEntity
import com.example.musicplayerapp.data.database.entities.PlaylistEntity
import com.example.musicplayerapp.data.database.entities.PlaylistTrackCrossRef

data class TrackWithPlaylists(
    @Embedded val track: MusicTrackEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlistId",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val playlists: List<PlaylistEntity>
)
