package com.media.app.playback

import com.media.app.domain.model.MediaTrack

data class PlayerState(
    val currentTrack: MediaTrack? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isBuffering: Boolean = false
)
