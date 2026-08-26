package com.media.app.core

sealed interface AppError {
    sealed interface NetworkError : AppError {
        data class HttpError(val code: Int, val message: String) : NetworkError
        object Timeout : NetworkError
        object NoConnection : NetworkError
        object Unknown : NetworkError
    }

    sealed interface DatabaseError : AppError {
        data class ReadFailed(val reason: String) : DatabaseError
        data class WriteFailed(val reason: String) : DatabaseError
    }

    sealed interface PlayerError : AppError {
        data class PlaybackFailed(val code: Int, val message: String) : PlayerError
        object SourceNotSupported : PlayerError
    }

    sealed interface ResolverError : AppError {
        object YtDlpFailed : ResolverError
        object PipedFailed : ResolverError
        object InvalidUrl : ResolverError
    }
}
