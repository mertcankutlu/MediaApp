package com.media.app

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MediaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val stackTrace = Log.getStackTraceString(throwable)
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(KEY_LAST_CRASH, stackTrace.take(MAX_CRASH_TEXT_LENGTH))
                    .putString(KEY_LAST_CRASH_THREAD, thread.name)
                    .apply()
            } catch (_: Throwable) {
                // Crash reporting must never become the cause of another crash.
            }

            previousHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        private const val PREFS_NAME = "media_app_diagnostics"
        private const val KEY_LAST_CRASH = "last_crash"
        private const val KEY_LAST_CRASH_THREAD = "last_crash_thread"
        private const val MAX_CRASH_TEXT_LENGTH = 12000

        fun lastCrash(application: Application): String? =
            application.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_LAST_CRASH, null)

        fun lastCrashThread(application: Application): String? =
            application.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_LAST_CRASH_THREAD, null)

        fun clearLastCrash(application: Application) {
            application.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .remove(KEY_LAST_CRASH)
                .remove(KEY_LAST_CRASH_THREAD)
                .apply()
        }
    }
}
