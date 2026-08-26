package com.media.app.domain.repository

import com.media.app.core.Result
import com.media.app.domain.model.MediaTrack

/**
 * Piped API üzerinden arama ve keşif işlemleri için sözleşme.
 */
interface SearchRepository {
    suspend fun search(query: String): Result<List<MediaTrack>>
}
