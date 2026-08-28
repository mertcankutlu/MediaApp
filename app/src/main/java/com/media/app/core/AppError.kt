package com.media.app.core

sealed interface AppError {

    sealed interface PlaybackError : AppError {
        data class InitializationFailed(val message: String) : PlaybackError
        data class SourceNotFound(val message: String) : PlaybackError
        data class MediaCodecError(val message: String) : PlaybackError
    }

    sealed interface DatabaseError : AppError {
        data class ReadFailed(val message: String) : DatabaseError
        data class WriteFailed(val message: String) : DatabaseError
    }

    sealed interface NetworkError : AppError {
        data class NoInternet(val message: String = "İnternet bağlantısı yok") : NetworkError
        data class Timeout(val message: String = "Bağlantı zaman aşımına uğradı") : NetworkError
        data class ServerError(val message: String) : NetworkError
    }

    sealed interface RemoteSourceError : AppError {
        data class ExtractorFailed(val message: String) : RemoteSourceError
        data class StreamNotFound(val message: String) : RemoteSourceError
        data class RateLimited(val message: String = "İstek limiti aşıldı") : RemoteSourceError
    }

    sealed interface SyncError : AppError {
        data class PermissionDenied(val message: String) : SyncError
        data class StorageReadFailed(val message: String) : SyncError
    }
}
