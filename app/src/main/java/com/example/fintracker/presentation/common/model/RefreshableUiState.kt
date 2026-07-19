package com.example.fintracker.presentation.common.model

// Хранит основное состояние экрана и отдельный признак фонового обновления.
data class RefreshableUiState<T>(
    val dataState: UiState<T> = UiState.Loading,
    val isRefreshing: Boolean = false
)
