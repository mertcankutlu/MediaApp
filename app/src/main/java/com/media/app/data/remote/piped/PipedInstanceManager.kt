package com.media.app.data.remote.piped

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipedInstanceManager @Inject constructor() {

    // Keep a curated fallback set from Piped's current public-instance list.
    // The first entries are preferred; failed entries enter cooldown.
    private val defaultInstances = listOf(
        "https://pipedapi.kavin.rocks",
        "https://pipedapi.leptons.xyz",
        "https://pipedapi.nosebs.ru",
        "https://pipedapi-libre.kavin.rocks",
        "https://piped-api.privacy.com.de",
        "https://pipedapi.adminforge.de",
        "https://api.piped.yt",
        "https://pipedapi.drgns.space",
        "https://pipedapi.owo.si",
        "https://pipedapi.ducks.party"
    )

    private val failedInstancesCooldown = ConcurrentHashMap<String, Long>()
    private val cooldownDurationMs = 3 * 60 * 1000L

    fun getInstanceCount(): Int = defaultInstances.size

    /**
     * Returns every currently healthy instance in deterministic priority order.
     * Each call returns a snapshot, so one request can try each instance at most once.
     */
    fun getHealthyBaseUrls(): List<String> {
        val now = System.currentTimeMillis()

        failedInstancesCooldown.forEach { (url, expiresAt) ->
            if (now >= expiresAt) {
                failedInstancesCooldown.remove(url, expiresAt)
            }
        }

        return defaultInstances.filter { url ->
            !failedInstancesCooldown.containsKey(url)
        }
    }

    /** Backward-compatible single-instance accessor. */
    fun getHealthyBaseUrl(): String =
        getHealthyBaseUrls().firstOrNull() ?: defaultInstances.first()

    fun reportFailure(baseUrl: String) {
        val cleanUrl = baseUrl.trimEnd('/')
        failedInstancesCooldown[cleanUrl] = System.currentTimeMillis() + cooldownDurationMs
    }
}
