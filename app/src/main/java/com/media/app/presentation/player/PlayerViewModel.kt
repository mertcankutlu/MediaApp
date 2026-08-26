package com.media.app.presentation.player

import androidx.lifecycle.ViewModel
import com.media.app.domain.model.MediaTrack
import com.media.app.playback.MediaControllerManager
import com.media.app.playback.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = mediaControllerManager.playerState

    fun playSampleTrack() {
        val sampleTrack = MediaTrack(
            id = "test_audio_1",
            title = "Test Ses Akışı",
            artist = "MediaApp Motoru",
            thumbnailUrl = "",
            durationSeconds = 180,
            sourceUrl = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"
        )
        mediaControllerManager.playTrack(sampleTrack)
    }

    fun togglePlayPause() {
        mediaControllerManager.togglePlayPause()
    }

    override fun onCleared() {
        super.onCleared()
        // ViewModel ömrü bittiğinde gerekirse temizlik işlemleri
    }
}
