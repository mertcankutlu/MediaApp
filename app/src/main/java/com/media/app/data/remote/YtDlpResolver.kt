package com.media.app.data.remote

import android.content.Context
import com.media.app.core.AppError
import com.media.app.core.Result
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YtDlpResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isInitialized = false

    @Synchronized
    fun initialize() {
        if (!isInitialized) {
            try {
                YoutubeDL.getInstance().init(context)
                isInitialized = true
            } catch (e: Exception) {
                // İlklendirme hatası
            }
        }
    }

    suspend fun resolveStreamUrl(videoId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            initialize()
            val request = YoutubeDLRequest("https://www.youtube.com/watch?v=$videoId").apply {
                addOption("-f", "bestaudio")
                addOption("--get-url")
            }
            val response = YoutubeDL.getInstance().execute(request)
            val streamUrl = response.out.trim()

            if (streamUrl.isNotEmpty()) {
                Result.success(streamUrl)
            } else {
                Result.failure(AppError.RemoteSourceError.StreamNotFound("yt-dlp URL üretemedi."))
            }
        } catch (e: Exception) {
            Result.failure(AppError.RemoteSourceError.ExtractorFailed("yt-dlp hatası: ${e.message}"))
        }
    }
}
