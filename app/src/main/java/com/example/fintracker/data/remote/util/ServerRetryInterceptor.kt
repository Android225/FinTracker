package com.example.fintracker.data.remote.util

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ServerRetryInterceptor @Inject constructor() : Interceptor {
    private var maxRetries: Int = ServerRetryPolicy.MAX_RETRIES
    private var retryDelayMillis: Long = ServerRetryPolicy.DELAY_MILLIS

    internal constructor(maxRetries: Int, retryDelayMillis: Long) : this() {
        this.maxRetries = maxRetries
        this.retryDelayMillis = retryDelayMillis
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        if (request.method !in IDEMPOTENT_METHODS) return response

        var retries = 0

        while (response.code == HTTP_INTERNAL_SERVER_ERROR && retries < maxRetries) {
            response.close()
            retries += 1
            try {
                Thread.sleep(retryDelayMillis)
            } catch (interrupted: InterruptedException) {
                Thread.currentThread().interrupt()
                throw interrupted
            }
            response = chain.proceed(request)
        }
        return response
    }

    private companion object {
        const val HTTP_INTERNAL_SERVER_ERROR = 500
        val IDEMPOTENT_METHODS = setOf("GET", "PUT", "DELETE", "HEAD", "OPTIONS")
    }
}
