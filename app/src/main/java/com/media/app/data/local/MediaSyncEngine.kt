package com.media.app.data.local

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSyncEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaDao: MediaDao,
    private val scanner: MediaStoreScanner
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var debounceJob: Job? = null

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            triggerSyncWithDebounce()
        }
    }

    fun startObserving() {
        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
        // Başlangıç ilk senkronizasyonu
        triggerSyncWithDebounce()
    }

    fun stopObserving() {
        context.contentResolver.unregisterContentObserver(contentObserver)
    }

    fun triggerSyncWithDebounce() {
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(500L) // Çoklu dosya işlemlerinde ardışık tetiklemeleri önleyen debounce
            performReconcile()
        }
    }

    suspend fun performReconcile() {
        val scannedTracks = scanner.scanAudioFiles()
        mediaDao.reconcile(scannedTracks)
    }
}
