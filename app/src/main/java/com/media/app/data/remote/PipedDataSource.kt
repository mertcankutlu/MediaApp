package com.media.app.data.remote

import com.media.app.core.AppError
import com.media.app.core.Result
import com.media.app.data.remote.api.PipedApiService
import com.media.app.data.remote.piped.PipedInstanceManager
import com.media.app.domain.model.MediaTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipedDataSource @Inject constructor(
    private val pipedApiService: PipedApiService,
    private val instanceManager: PipedInstanceManager
) {
    suspend fun searchTracks(query: String): Result<List<MediaTrack>> = withContext(Dispatchers.IO) {
        val totalInstances = instanceManager.getInstanceCount()
        var lastErrorMessage = "Bilinmeyen hata"

        for (i in 0 until totalInstances) {
            val baseUrl = instanceManager.getHealthyBaseUrl()
            val endpoint = "$baseUrl/search"

            try {
                val response = pipedApiService.search(fullUrl = endpoint, query = query)
                val tracks = response.mapNotNull { dto ->
                    val rawUrl = dto.url ?: return@mapNotNull null
                    val videoId = when {
                        rawUrl.contains("watch?v=") -> rawUrl.substringAfter("watch?v=")
                        rawUrl.startsWith("/") -> rawUrl.removePrefix("/")
                        else -> rawUrl
                    }

                    if (videoId.isBlank()) return@mapNotNull null

                    MediaTrack(
                        id = videoId,
                        title = dto.title ?: "Bilinmeyen Başlık",
                        artist = dto.uploaderName ?: "Bilinmeyen Sanatçı",
                        thumbnailUrl = dto.thumbnail ?: "",
                        durationSeconds = dto.duration ?: 0L,
                        sourceUrl = null,
                        isOffline = false
                    )
                }.distinctBy { it.id }

                return@withContext Result.success(tracks)
            } catch (e: Exception) {
                lastErrorMessage = e.message ?: "Ağ hatası"
                instanceManager.reportFailure(baseUrl)
            }
        }

        Result.failure(AppError.NetworkError.ServerError("Tüm Piped sunucuları başarısız: $lastErrorMessage"))
    }

    suspend fun resolveAudioUrl(videoId: String): Result<String> = withContext(Dispatchers.IO) {
        val totalInstances = instanceManager.getInstanceCount()
        var lastErrorMessage = "Bilinmeyen hata"

        for (i in 0 until totalInstances) {
            val baseUrl = instanceManager.getHealthyBaseUrl()
            val endpoint = "$baseUrl/streams"

            try {
                val response = pipedApiService.getStreams(fullUrl = endpoint, videoId = videoId)
                val audioStream = response.audioStreams
                    ?.filter { !it.url.isNullOrEmpty() }
                    ?.maxByOrNull { it.bitrate ?: 0 }

                if (audioStream?.url != null) {
                    return@withContext Result.success(audioStream.url)
                } else {
                    instanceManager.reportFailure(baseUrl)
                }
            } catch (e: Exception) {
                lastErrorMessage = e.message ?: "Akış hatası"
                instanceManager.reportFailure(baseUrl)
            }
        }

        Result.failure(AppError.RemoteSourceError.StreamNotFound("Ses akışı bulunamadı: $lastErrorMessage"))
    }
}
