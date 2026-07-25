package com.example.fintracker.data.remote.util

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ServerRetryInterceptor @Inject constructor() : Interceptor {
    private var maxRetries: Int = MAX_RETRIES
    private var retryDelayMillis: Long = RETRY_DELAY_MILLIS

    internal constructor(maxRetries: Int, retryDelayMillis: Long) : this() {
        this.maxRetries = maxRetries
        this.retryDelayMillis = retryDelayMillis
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
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
            response = chain.proceed(chain.request())
        }
        return response
    }

    private companion object {
        const val HTTP_INTERNAL_SERVER_ERROR = 500
        const val MAX_RETRIES = 3
        val RETRY_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(2)
    }
}
