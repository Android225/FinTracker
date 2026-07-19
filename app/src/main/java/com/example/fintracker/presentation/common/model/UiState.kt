package com.example.fintracker.presentation.common.model

import com.example.fintracker.domain.error.AppError

// Общие состояния загрузки данных для экранов.
sealed interface UiState<out T> {
    // Данные загружаются.
    data object Loading : UiState<Nothing>
    // Данные успешно получены.
    data class Content<T>(val data: T) : UiState<T>
    // Данные успешно загружены, но основная коллекция пуста.
    data class Empty<T>(val data: T? = null) : UiState<T>
    // Загрузка завершилась ошибкой.
    data class Error(val error: AppError) : UiState<Nothing>
}
