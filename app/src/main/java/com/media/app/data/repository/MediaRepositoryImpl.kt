package com.media.app.data.repository

import com.media.app.core.AppError
import com.media.app.core.Result
import com.media.app.data.local.MediaDao
import com.media.app.data.local.MediaEntity
import com.media.app.data.local.MediaStoreScanner
import com.media.app.domain.model.MediaTrack
import com.media.app.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao,
    private val mediaStoreScanner: MediaStoreScanner
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
            val scannedTracks = mediaStoreScanner.scanAudioFiles()
            
            if (scannedTracks.isNotEmpty()) {
                mediaDao.insertTracks(scannedTracks)
            }

            val currentValidIds = scannedTracks.map { it.id }
            mediaDao.deleteRemovedLocalTracks(currentValidIds)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.DatabaseError.WriteFailed(e.message ?: "Senkronizasyon hatası"))
        }
    }
}
