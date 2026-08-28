package com.media.app.domain.usecase

import com.media.app.core.Result
import com.media.app.domain.model.MediaTrack
import com.media.app.domain.repository.MediaRepository
import javax.inject.Inject

class SearchTracksUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(query: String): Result<List<MediaTrack>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }
        return mediaRepository.searchRemoteTracks(query.trim())
    }
}
