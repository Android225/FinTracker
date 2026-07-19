package com.example.fintracker.presentation.common.model

import com.example.fintracker.domain.error.AppError

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Content<T>(val data: T) : UiState<T>
    data class Empty<T>(val data: T? = null) : UiState<T>
    data class Error(val error: AppError) : UiState<Nothing>
}
