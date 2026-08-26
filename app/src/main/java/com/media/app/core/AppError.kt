package com.media.app.core

sealed interface AppError {
    // Altyapı ve Ağ Hataları (Fallback tetikleyebilir)
    sealed interface NetworkError : AppError {
        data class HttpError(val code: Int, val message: String) : NetworkError
        object Timeout : NetworkError
        object NoConnection : NetworkError
        object Unknown : NetworkError
    }

    // Veritabanı ve Yerel Depolama Hataları
    sealed interface DatabaseError : AppError {
        data class ReadFailed(val reason: String) : DatabaseError
        data class WriteFailed(val reason: String) : DatabaseError
    }

    // Oynatıcı Hataları
    sealed interface PlayerError : AppError {
        data class PlaybackFailed(val code: Int, val message: String) : PlayerError
        object SourceNotSupported : PlayerError
    }

    // Stream & Keşif Hataları (Manifesto Semantik Ayrımı)
    sealed interface ResolverError : AppError {
        // Fallback tetikleyen sistem hataları
        object StreamResolveFailed : ResolverError
        object SearchUnavailable : ResolverError
        object YtDlpFailed : ResolverError
        object PipedFailed : ResolverError

        // Fallback zincirini ANINDA KIRAN kullanıcı/içerik hataları
        object UserCancelled : ResolverError
        object InvalidVideoId : ResolverError
        object ContentUnavailable : ResolverError
    }
}
