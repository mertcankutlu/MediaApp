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
import java.util.concurrent.atomic.AtomicBoolean
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
    private val isObserving = AtomicBoolean(false)

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            triggerSyncWithDebounce()
        }
    }

    @Synchronized
    fun startObserving() {
        if (isObserving.compareAndSet(false, true)) {
            try {
                context.contentResolver.registerContentObserver(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    true,
                    contentObserver
                )
            } catch (e: SecurityException) {
                isObserving.set(false)
                return
            }
            triggerSyncWithDebounce()
        }
    }

    @Synchronized
    fun stopObserving() {
        if (isObserving.compareAndSet(true, false)) {
            try {
                context.contentResolver.unregisterContentObserver(contentObserver)
            } catch (e: Exception) {
                // Güvenli unregister
            }
            debounceJob?.cancel()
        }
    }

    fun triggerSyncWithDebounce() {
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(500L) // UI takılmasını ve peş peşe taramaları engelleyen debounce
            performReconcile()
        }
    }

    suspend fun performReconcile() {
        val scannedTracks = scanner.scanAudioFiles()
        mediaDao.reconcile(scannedTracks)
    }
}
