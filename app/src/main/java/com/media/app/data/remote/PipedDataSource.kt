package com.media.app.data.remote

import com.media.app.core.AppError
import com.media.app.core.Result
import com.media.app.data.remote.api.PipedApiService
import com.media.app.domain.model.MediaTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipedDataSource @Inject constructor(
    private val pipedApiService: PipedApiService
) {
    suspend fun searchTracks(query: String): Result<List<MediaTrack>> = withContext(Dispatchers.IO) {
        try {
            val response = pipedApiService.search(query = query)
            val tracks = response.mapNotNull { dto ->
                val videoId = dto.url?.substringAfter("watch?v=") ?: return@mapNotNull null
                MediaTrack(
                    id = videoId,
                    title = dto.title ?: "Bilinmeyen Başlık",
                    artist = dto.uploaderName ?: "Bilinmeyen Sanatçı",
                    thumbnailUrl = dto.thumbnail ?: "",
                    durationSeconds = dto.duration ?: 0L,
                    sourceUrl = null,
                    isOffline = false
                )
            }
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(AppError.NetworkError.ServerError("Piped arama hatası: ${e.message}"))
        }
    }

    suspend fun resolveAudioUrl(videoId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = pipedApiService.getStreams(videoId = videoId)
            val audioStream = response.audioStreams
                ?.filter { !it.url.isNullOrEmpty() }
                ?.maxByOrNull { it.bitrate ?: 0 }

            if (audioStream?.url != null) {
                Result.success(audioStream.url)
            } else {
                Result.failure(AppError.RemoteSourceError.StreamNotFound("Uygun ses akışı bulunamadı."))
            }
        } catch (e: Exception) {
            Result.failure(AppError.NetworkError.ServerError("Piped akış çözümleme hatası: ${e.message}"))
        }
    }
}
