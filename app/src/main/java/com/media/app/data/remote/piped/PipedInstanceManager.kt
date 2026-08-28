package com.media.app.data.remote.piped

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipedInstanceManager @Inject constructor() {

    private val defaultInstances = listOf(
        "https://pipedapi.kavin.rocks",
        "https://api.piped.privacy.com.de",
        "https://piped-api.lunar.icu",
        "https://api.piped.yt"
    )

    private val failedInstancesCooldown = ConcurrentHashMap<String, Long>()
    private val cooldownDurationMs = 5 * 60 * 1000L // 5 Dakika Cooldown

    fun getInstanceCount(): Int = defaultInstances.size

    fun getHealthyBaseUrl(): String {
        val currentTime = System.currentTimeMillis()

        failedInstancesCooldown.entries.removeIf { currentTime > it.value }

        val healthyInstance = defaultInstances.firstOrNull { !failedInstancesCooldown.containsKey(it) }
            ?: defaultInstances.first()

        return healthyInstance
    }

    fun reportFailure(baseUrl: String) {
        val cleanUrl = baseUrl.trimEnd('/')
        failedInstancesCooldown[cleanUrl] = System.currentTimeMillis() + cooldownDurationMs
    }
}
