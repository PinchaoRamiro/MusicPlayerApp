package com.example.musicplayerapp.di

import android.content.Context
import com.example.musicplayerapp.data.local.MusicMediaStoreDataSource
import com.example.musicplayerapp.data.repository.MusicRepository
import com.example.musicplayerapp.domain.usecase.ScanMusicUseCase

class AppModule(context: Context) {

    // ✅ Primero creamos el DataSource con el contexto
    private val musicMediaStoreDataSource = MusicMediaStoreDataSource(context)

    // ✅ Luego lo pasamos al repositorio
    private val musicRepository = MusicRepository(musicMediaStoreDataSource)

    // ✅ Finalmente creamos el caso de uso
    val scanMusicUseCase = ScanMusicUseCase(musicRepository)
}
