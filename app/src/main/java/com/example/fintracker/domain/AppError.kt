package com.example.fintracker.domain

sealed interface AppError {
    data object Network : AppError
    data object Unauthorized : AppError
    data object NotFound : AppError
    data object Server : AppError
    data class Unknown(val message: String? = null) : AppError
}
