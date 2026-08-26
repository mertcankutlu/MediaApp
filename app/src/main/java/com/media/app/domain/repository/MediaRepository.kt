package com.media.app.domain.repository

import com.media.app.core.Result
import com.media.app.domain.model.MediaTrack
import kotlinx.coroutines.flow.Flow

/**
 * Yerel medya kütüphanesi (Room + MediaStore) işlemleri için sözleşme.
 * Manifesto: "Room Authoritative" (Tek gerçeklik kaynağı Room'dur).
 */
interface MediaRepository {
    // UI sadece bu Flow'u dinler, durumu her an otomatik güncellenir.
    fun getLocalMedia(): Flow<List<MediaTrack>>
    
    suspend fun getTrackById(id: String): Result<MediaTrack>
    
    // Arka plandaki Sync Engine'in MediaStore'u tarayıp Room'u güncelleyeceği fonksiyon
    suspend fun syncWithMediaStore(): Result<Unit>
}
