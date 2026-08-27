package com.media.app.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.app.data.local.MediaSyncEngine
import com.media.app.domain.model.MediaTrack
import com.media.app.domain.repository.MediaRepository
import com.media.app.playback.MediaControllerManager
import com.media.app.playback.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val mediaRepository: MediaRepository,
    private val syncEngine: MediaSyncEngine
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = mediaControllerManager.playerState

    val localTracks: StateFlow<List<MediaTrack>> = mediaRepository.getLocalMedia()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun startObservingStorage() {
        syncEngine.startObserving()
    }

    fun syncTracks() {
        viewModelScope.launch {
            mediaRepository.syncWithMediaStore()
        }
    }

    fun playTrack(track: MediaTrack) {
        mediaControllerManager.playTrack(track)
    }

    fun togglePlayPause() {
        mediaControllerManager.togglePlayPause()
    }

    override fun onCleared() {
        super.onCleared()
        syncEngine.stopObserving()
    }
}
