package com.example.musicplayerapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayerapp.domain.usecase.ScanMusicUseCase

class MusicListViewModelFactory(
    private val application: Application,
    private val scanMusicUseCase: ScanMusicUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicListViewModel::class.java)) {
            return MusicListViewModel(application, scanMusicUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}