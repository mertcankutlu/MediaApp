package com.media.app.domain.model

/**
 * WorkManager'ın Room'a yazacağı, UI'ın da Room'dan okuyacağı indirme durumları.
 */
enum class DownloadStatus {
    NONE,           // İndirme yok
    PENDING,        // Sıraya alındı, bekliyor
    DOWNLOADING,    // İndiriliyor
    PAUSED,         // Duraklatıldı
    COMPLETED,      // Başarıyla bitti
    FAILED          // Hata aldı
}
