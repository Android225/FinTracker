package com.example.fintracker.presentation.common.model

import com.example.fintracker.domain.error.AppError

// Общие состояния загрузки данных для экранов.
sealed interface UiState<out T> {
    // Данные загружаются.
    data object Loading : UiState<Nothing>
    // Данные успешно получены.
    data class Content<T>(val data: T) : UiState<T>
    // Загруженный список пуст.
    data object Empty : UiState<Nothing>
    // Загрузка завершилась ошибкой.
    data class Error(val error: AppError) : UiState<Nothing>
}
