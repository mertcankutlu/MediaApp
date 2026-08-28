package com.media.app.domain.usecase

import com.media.app.core.Result
import com.media.app.domain.repository.MediaRepository
import javax.inject.Inject

class ResolveStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(videoId: String): Result<String> {
        return mediaRepository.resolveStreamUrl(videoId)
    }
}
