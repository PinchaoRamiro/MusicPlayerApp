package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.model.MusicTrack
import com.example.musicplayerapp.data.repository.MusicRepository

class ScanMusicUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(): List<MusicTrack> {
        return repository.fetchAllTracks()
    }
}

// Dominio – Otros casos de uso futuros
//Luego podrás agregar otros casos como:
//
//PlayMusicUseCase
//
//PauseMusicUseCase
//
//CreatePlaylistUseCase
//
//Esto mantiene el principio de responsabilidad única y separa la lógica de negocio del UI y Data.