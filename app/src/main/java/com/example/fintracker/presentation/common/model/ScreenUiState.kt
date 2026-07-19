package com.example.fintracker.presentation.common.model

import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.flow.MutableStateFlow

data class ScreenUiState<T>(
    val loadState: UiState<T> = UiState.Loading,
    val isRefreshing: Boolean = false
)

// Начинает фоновое обновление, не скрывая уже показанные данные.
internal fun <T> MutableStateFlow<ScreenUiState<T>>.startRefreshing(): UiState<T> {
    val previousState = value.loadState
    value = if (previousState is UiState.Content || previousState is UiState.Empty) {
        value.copy(isRefreshing = true)
    } else {
        ScreenUiState(loadState = UiState.Loading)
    }
    return previousState
}

// При refresh-ошибке сохраняет старые данные и передаёт ошибку для Snackbar.
internal suspend fun <T> MutableStateFlow<ScreenUiState<T>>.updateFrom(
    result: DataResult<T>,
    previousState: UiState<T>,
    isEmpty: (T) -> Boolean,
    onRefreshError: suspend (AppError) -> Unit
) {
    val updatedState = when (result) {
        is DataResult.Success -> {
            if (isEmpty(result.data)) UiState.Empty(result.data) else UiState.Content(result.data)
        }
        is DataResult.Failure -> {
            if (previousState is UiState.Content || previousState is UiState.Empty) {
                onRefreshError(result.error)
                previousState
            } else {
                UiState.Error(result.error)
            }
        }
    }
    value = ScreenUiState(loadState = updatedState)
}
