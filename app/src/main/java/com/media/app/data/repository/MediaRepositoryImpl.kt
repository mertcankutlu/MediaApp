package com.media.app.data.repository

import com.media.app.core.AppError
import com.media.app.core.Result
import com.media.app.data.local.MediaDao
import com.media.app.data.local.MediaEntity
import com.media.app.data.local.MediaSyncEngine
import com.media.app.data.remote.PipedDataSource
import com.media.app.data.remote.YtDlpResolver
import com.media.app.domain.model.MediaTrack
import com.media.app.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao,
    private val syncEngine: MediaSyncEngine,
    private val pipedDataSource: PipedDataSource,
    private val ytDlpResolver: YtDlpResolver
) : MediaRepository {

    override fun getLocalMedia(): Flow<List<MediaTrack>> {
        val tracksFlow: Flow<List<MediaEntity>> = mediaDao.getAllTracksFlow()
        return tracksFlow.map { entities: List<MediaEntity> ->
            entities.map { entity: MediaEntity -> entity.toDomain() }
        }
    }

    override suspend fun getTrackById(id: String): Result<MediaTrack> {
        return try {
            val entity = mediaDao.getTrackById(id)
            if (entity != null) {
                Result.success(entity.toDomain())
            } else {
                Result.failure(AppError.DatabaseError.ReadFailed("Parça bulunamadı: $id"))
            }
        } catch (e: Exception) {
            Result.failure(AppError.DatabaseError.ReadFailed(e.message ?: "Bilinmeyen DB hatası"))
        }
    }

    override suspend fun syncWithMediaStore(): Result<Unit> {
        return try {
            syncEngine.performReconcile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.DatabaseError.WriteFailed(e.message ?: "Senkronizasyon hatası"))
        }
    }

    override suspend fun searchRemoteTracks(query: String): Result<List<MediaTrack>> {
        return pipedDataSource.searchTracks(query)
    }

    override suspend fun resolveStreamUrl(videoId: String): Result<String> {
        // 1. Birincil Deneme: Piped API (Hafif ve hızlı)
        val pipedResult = pipedDataSource.resolveAudioUrl(videoId)
        if (pipedResult is Result.Success) {
            return pipedResult
        }

        // 2. İkincil Deneme (Fallback): yt-dlp Motoru
        return ytDlpResolver.resolveStreamUrl(videoId)
    }
}
