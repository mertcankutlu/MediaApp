package com.media.app.data.remote.piped

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipedInstanceManager @Inject constructor() {

    private val defaultInstances = listOf(
        "https://pipedapi.kavin.rocks",
        "https://api.piped.privacydev.net",
        "https://pipedapi.leptons.xyz",
        "https://piped-api.lunar.icu"
    )

    private val failedInstancesCooldown = ConcurrentHashMap<String, Long>()
    private val cooldownDurationMs = 3 * 60 * 1000L

    fun getInstanceCount(): Int = defaultInstances.size

    fun getHealthyBaseUrl(): String {
        val now = System.currentTimeMillis()

        // Avoid ConcurrentHashMap.entries.removeIf() here. Using the map's
        // conditional remove keeps this compatible with older Android runtimes
        // and is safe if another coroutine reports a failure concurrently.
        failedInstancesCooldown.forEach { (url, expiresAt) ->
            if (now >= expiresAt) {
                failedInstancesCooldown.remove(url, expiresAt)
            }
        }

        return defaultInstances.firstOrNull { url ->
            !failedInstancesCooldown.containsKey(url)
        } ?: defaultInstances.first()
    }

    fun reportFailure(baseUrl: String) {
        val cleanUrl = baseUrl.trimEnd('/')
        failedInstancesCooldown[cleanUrl] = System.currentTimeMillis() + cooldownDurationMs
    }
}
