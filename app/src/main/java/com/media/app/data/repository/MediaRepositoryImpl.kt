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
        return mediaDao.getAllTracksFlow().map { entities ->
            val domainList = ArrayList<MediaTrack>(entities.size)
            for (entity in entities) {
                domainList.add(entity.toDomain())
            }
            domainList
        }
    }

    override suspend fun getTrackById(id: String): Result<MediaTrack> {
        return try {
            val entity = mediaDao.getTrackById(id)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Failure(AppError.DatabaseError.ReadFailed("Parça bulunamadı: $id"))
            }
        } catch (e: Exception) {
            Result.Failure(AppError.DatabaseError.ReadFailed(e.message ?: "Bilinmeyen DB hatası"))
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

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(AppError.DatabaseError.WriteFailed(e.message ?: "Senkronizasyon hatası"))
        }
    }
}
