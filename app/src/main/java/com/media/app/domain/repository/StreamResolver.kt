package com.media.app.domain.repository

import com.media.app.core.Result

/**
 * Video ID'sini alıp oynatılabilir saf ses/video akış URL'sine (Stream URL) dönüştürür.
 * İçeride Piped ve hata durumunda yt-dlp izolasyonunu yönetir.
 */
interface StreamResolver {
    suspend fun resolveUrl(videoId: String): Result<String>
}
