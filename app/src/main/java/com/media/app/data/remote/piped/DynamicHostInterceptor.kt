package com.media.app.data.remote.piped

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicHostInterceptor @Inject constructor(
    private val instanceManager: PipedInstanceManager
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val totalAttempts = instanceManager.getInstanceCount()
        var lastException: IOException? = null

        for (attempt in 0 until totalAttempts) {
            val currentBaseUrl = instanceManager.getHealthyBaseUrl()
            val currentHost = currentBaseUrl.toHttpUrlOrNull() ?: continue

            val newUrl = originalRequest.url.newBuilder()
                .scheme(currentHost.scheme)
                .host(currentHost.host)
                .port(currentHost.port)
                .build()

            val newRequest = originalRequest.newBuilder().url(newUrl).build()

            try {
                val response = chain.proceed(newRequest)
                
                if (response.isSuccessful) {
                    return response
                }

                // 429 (Rate Limit) veya 5xx sunucu hatasında instance'ı cezalandır ve bir sonrakini dene
                if (response.code == 429 || response.code in 500..599) {
                    response.close()
                    instanceManager.reportFailure(currentBaseUrl)
                    continue
                }

                // 4xx istemci hatalarında retry yapmadan doğrudan dön
                return response
            } catch (e: IOException) {
                instanceManager.reportFailure(currentBaseUrl)
                lastException = e
            }
        }

        throw lastException ?: IOException("Tüm Piped instance'ları yanıt vermedi.")
    }
}
