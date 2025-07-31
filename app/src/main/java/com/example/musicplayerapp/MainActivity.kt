package com.example.musicplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.musicplayerapp.core.utils.PermissionsManager
import com.example.musicplayerapp.di.AppModule
import com.example.musicplayerapp.viewmodel.MusicListViewModel
import com.example.musicplayerapp.viewmodel.MusicListViewModelFactory
import com.example.musicplayerapp.ui.screen.MusicListScreen

class MainActivity : ComponentActivity() {

    private val appModule by lazy { AppModule(this) }

    private val musicListViewModel: MusicListViewModel by viewModels {
        MusicListViewModelFactory(
            this.application,
            appModule.scanMusicUseCase
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            musicListViewModel.loadMusic()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContent {
            MusicListScreen(viewModel = musicListViewModel)
        }
    }

    private fun requestPermissions() {
        val perms = PermissionsManager.getAudioPermissions()
        if (!PermissionsManager.hasPermissions(this)) {
            requestPermissionLauncher.launch(perms)
        } else {
            musicListViewModel.loadMusic()
        }
    }
}
