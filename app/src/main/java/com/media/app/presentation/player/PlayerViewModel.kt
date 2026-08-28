package com.media.app.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.app.core.Result
import com.media.app.data.local.MediaSyncEngine
import com.media.app.domain.model.MediaTrack
import com.media.app.domain.repository.MediaRepository
import com.media.app.domain.usecase.SearchTracksUseCase
import com.media.app.playback.MediaControllerManager
import com.media.app.playback.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val mediaRepository: MediaRepository,
    private val searchTracksUseCase: SearchTracksUseCase,
    private val syncEngine: MediaSyncEngine
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = mediaControllerManager.playerState

    val localTracks: StateFlow<List<MediaTrack>> = mediaRepository.getLocalMedia()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MediaTrack>>(emptyList())
    val searchResults: StateFlow<List<MediaTrack>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            delay(500L) // Kullanıcı yazarken istek yağmurunu önleyen debounce
            _isSearching.value = true
            _errorMessage.value = null

            when (val result = searchTracksUseCase(query)) {
                is Result.Success -> {
                    _searchResults.value = result.data
                }
                is Result.Failure -> {
                    _errorMessage.value = "Arama başarısız oldu."
                }
            }
            _isSearching.value = false
        }
    }

    fun playRemoteTrack(track: MediaTrack) {
        viewModelScope.launch {
            _errorMessage.value = null
            when (val resolveResult = mediaRepository.resolveStreamUrl(track.id)) {
                is Result.Success -> {
                    val playableTrack = track.copy(sourceUrl = resolveResult.data)
                    mediaControllerManager.playTrack(playableTrack)
                }
                is Result.Failure -> {
                    _errorMessage.value = "Ses akışı çözülemedi."
                }
            }
        }
    }

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
