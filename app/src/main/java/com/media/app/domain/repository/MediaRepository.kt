package com.media.app.domain.repository

import com.media.app.core.Result
import com.media.app.domain.model.MediaTrack
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getLocalMedia(): Flow<List<MediaTrack>>
    suspend fun getTrackById(id: String): Result<MediaTrack>
    suspend fun syncWithMediaStore(): Result<Unit>
    
    // Faz 3: Uzak Kaynak ve Akış Çözümleme
    suspend fun searchRemoteTracks(query: String): Result<List<MediaTrack>>
    suspend fun resolveStreamUrl(videoId: String): Result<String>
}
