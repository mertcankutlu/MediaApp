package com.media.app.domain.model

/**
 * Uygulama genelinde kullanılacak tek ve mutlak Medya (Ses/Video) modeli.
 * UI, Room ve Playback servisleri sadece bu model üzerinden konuşur.
 */
data class MediaTrack(
    val id: String, // YouTube Video ID'si veya yerel dosya ID'si
    val title: String,
    val artist: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val sourceUrl: String? = null, // Akış URL'si (yt-dlp veya Piped çözdükten sonra dolar)
    val isOffline: Boolean = false, // Dosya cihaza indiyse true olur
    val localPath: String? = null // İndirilen dosyanın cihazdaki konumu
)
