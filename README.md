🎵 MusicPlayerApp
MusicPlayerApp es una aplicación de música para Android desarrollada en Kotlin utilizando Jetpack Compose, MVVM y ExoPlayer.
Permite escanear canciones locales, reproducirlas en segundo plano con un servicio persistente, controles en la notificación multimedia, soporte de MediaSession y un footer dinámico que muestra la canción actual y controles básicos.

✨ Funcionalidades
🔎 Escaneo automático de canciones almacenadas en el dispositivo.

▶️ Reproducción, pausa, siguiente y anterior desde la app o notificación.

🎶 Visualización del título y artista en la notificación y la pantalla.

🔊 Soporte para control desde lock screen y auriculares (MediaSession).

🛠 Arquitectura MVVM con ViewModel, Service y ExoPlayer.

🎨 Interfaz moderna con Jetpack Compose y un footer de “Now Playing”.

⚡ Reproducción en segundo plano con servicio en primer plano (foreground).

🛠️ Tecnologías usadas
Kotlin

Jetpack Compose

ExoPlayer (Media3)

Android Service + MediaSessionCompat

MVVM Architecture

Kotlin Coroutines + StateFlow

Sincronizar dependencias de Gradle.

Ejecutar en un emulador o dispositivo físico (Android 8+).

📂 Estructura del proyecto
~~~bash
    app/
     ├── data/               # Modelos y acceso a datos
     │   └── model/          # Clase MusicTrack
     ├── domain/             # Casos de uso (use cases)
     │   └── usecase/        # ScanMusicUseCase
     ├── player/             # Lógica de reproducción
     │   ├── controller/     # MusicPlayerController (ExoPlayer)
     │   ├── service/        # MediaPlaybackService + NotificationHelper
     │   └── session/        # MediaSessionManager
     ├── ui/                 # Interfaz en Jetpack Compose
     │   ├── screen/         # Pantallas principales (MusicListScreen)
     │   └── components/     # Componentes reutilizables (NowPlayingFooter)
     ├── viewmodel/          # ViewModels (MusicListViewModel)
     └── MainActivity.kt
~~~

🧩 Características técnicas
Service (MediaPlaybackService) ejecuta la reproducción en segundo plano.

ExoPlayer gestiona la lista de canciones y el estado del reproductor.

NotificationHelper crea la notificación multimedia con botones (play, pause, next, prev).

MediaSessionManager permite integración con controles externos (auriculares, lock screen).

MVVM: MusicListViewModel conecta la capa de datos con la UI.

NowPlayingFooter muestra la canción actual con barra de progreso y botón de play/pause.
