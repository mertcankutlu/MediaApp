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
        var request = chain.request()
        val currentHost = instanceManager.getHealthyBaseUrl().toHttpUrlOrNull()

        if (currentHost != null) {
            val newUrl = request.url.newBuilder()
                .scheme(currentHost.scheme)
                .host(currentHost.host)
                .port(currentHost.port)
                .build()

            request = request.newBuilder().url(newUrl).build()
        }

        try {
            val response = chain.proceed(request)
            if (!response.isSuccessful && response.code in 500..599) {
                currentHost?.let { instanceManager.reportFailure("${it.scheme}://${it.host}") }
            }
            return response
        } catch (e: Exception) {
            currentHost?.let { instanceManager.reportFailure("${it.scheme}://${it.host}") }
            throw e
        }
    }
}
