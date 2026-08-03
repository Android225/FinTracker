package com.example.fintracker.di

import com.example.fintracker.BuildConfig
import com.example.fintracker.data.remote.api.FinanceApi
import com.example.fintracker.data.remote.auth.AccessTokenProvider
import com.example.fintracker.data.remote.auth.AuthInterceptor
import com.example.fintracker.data.remote.util.ServerRetryInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://shmr-finance.ru/api/v1/"
    private const val TIMEOUT_SECONDS = 30L

    @Provides
    @Singleton
    fun provideAccessTokenProvider(): AccessTokenProvider =
        AccessTokenProvider { BuildConfig.SHMR_AUTH_TOKEN }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        accessTokenProvider: AccessTokenProvider
    ): AuthInterceptor = AuthInterceptor(accessTokenProvider)

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            redactHeader(AuthInterceptor.AUTHORIZATION_HEADER)
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        serverRetryInterceptor: ServerRetryInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(serverRetryInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideFinanceApi(retrofit: Retrofit): FinanceApi =
        retrofit.create(FinanceApi::class.java)
}
