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
        var lastErrorMessage = "Bilinmeyen hata"
        val candidates = instanceManager.getHealthyBaseUrls()

        if (candidates.isEmpty()) {
            return@withContext Result.failure(
                AppError.NetworkError.ServerError("Geçici olarak kullanılabilir Piped sunucusu yok")
            )
        }

        for (baseUrl in candidates) {
            val endpoint = "$baseUrl/search"
            try {
                // Piped /search returns a SearchPage object; the actual results
                // are contained in its "items" field.
                val response = pipedApiService.search(fullUrl = endpoint, query = query)
                val tracks = response.items.orEmpty().mapNotNull { dto ->
                    val rawUrl = dto.url ?: return@mapNotNull null
                    val videoId = when {
                        rawUrl.contains("watch?v=") -> rawUrl.substringAfter("watch?v=").substringBefore('&')
                        rawUrl.startsWith("/") -> rawUrl.removePrefix("/").substringAfter("watch?v=").substringBefore('&')
                        else -> rawUrl.substringAfterLast("/watch?v=").substringBefore('&')
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
        var lastErrorMessage = "Bilinmeyen hata"
        val candidates = instanceManager.getHealthyBaseUrls()

        if (candidates.isEmpty()) {
            return@withContext Result.failure(
                AppError.NetworkError.ServerError("Geçici olarak kullanılabilir Piped sunucusu yok")
            )
        }

        for (baseUrl in candidates) {
            val endpoint = "$baseUrl/streams"
            try {
                val response = pipedApiService.getStreams(fullUrl = endpoint, videoId = videoId)
                val audioStream = response.audioStreams
                    ?.filter { !it.url.isNullOrEmpty() }
                    ?.maxByOrNull { it.bitrate ?: 0 }

                if (audioStream?.url != null) {
                    return@withContext Result.success(audioStream.url)
                }

                lastErrorMessage = "Ses akışı bulunamadı"
            } catch (e: Exception) {
                lastErrorMessage = e.message ?: "Akış hatası"
                instanceManager.reportFailure(baseUrl)
            }
        }

        Result.failure(AppError.RemoteSourceError.StreamNotFound("Ses akışı bulunamadı: $lastErrorMessage"))
    }
}
