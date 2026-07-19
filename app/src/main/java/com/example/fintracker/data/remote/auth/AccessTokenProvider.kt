package com.example.fintracker.data.remote.auth

fun interface AccessTokenProvider {
    fun getAccessToken(): String
}
