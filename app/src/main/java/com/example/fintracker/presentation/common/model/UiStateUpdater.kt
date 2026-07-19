package com.example.fintracker.presentation.common.model

import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.flow.MutableStateFlow

// Начинает загрузку, сохраняя уже показанный контент во время фонового обновления.
internal fun <T> MutableStateFlow<RefreshableUiState<T>>.startRefreshing(): UiState<T> {
    val previousState = value.dataState
    value = if (previousState is UiState.Content || previousState is UiState.Empty) {
        value.copy(isRefreshing = true)
    } else {
        RefreshableUiState(dataState = UiState.Loading)
    }
    return previousState
}

// Завершает загрузку новым состоянием либо сохраняет старые данные при refresh-ошибке.
internal suspend fun <T> MutableStateFlow<RefreshableUiState<T>>.updateFrom(
    result: DataResult<T>,
    previousState: UiState<T>,
    isEmpty: (T) -> Boolean,
    onRefreshError: suspend (ScreenEffect.ShowError) -> Unit
) {
    val updatedState = when (result) {
        is DataResult.Success -> {
            if (isEmpty(result.data)) UiState.Empty(result.data) else UiState.Content(result.data)
        }
        is DataResult.Failure -> {
            if (previousState is UiState.Content || previousState is UiState.Empty) {
                onRefreshError(ScreenEffect.ShowError(result.error))
                previousState
            } else {
                UiState.Error(result.error)
            }
        }
    }
    value = RefreshableUiState(dataState = updatedState)
}
