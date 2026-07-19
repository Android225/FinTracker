package com.example.fintracker.data.remote.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val accessTokenProvider: AccessTokenProvider
) : Interceptor {
    // Добавляет Bearer-токен ко всем запросам, если токен задан в конфигурации сборки.
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = accessTokenProvider.getAccessToken().trim()
        val request = if (token.isEmpty()) {
            chain.request()
        } else {
            chain.request()
                .newBuilder()
                .header(AUTHORIZATION_HEADER, "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
