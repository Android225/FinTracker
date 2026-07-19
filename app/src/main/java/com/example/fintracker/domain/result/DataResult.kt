package com.example.fintracker.domain.result

import com.example.fintracker.domain.error.AppError

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Failure(val error: AppError) : DataResult<Nothing>
}
