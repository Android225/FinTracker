package com.example.fintracker.presentation.common.model

data class RefreshableUiState<T>(
    val dataState: UiState<T> = UiState.Loading,
    val isRefreshing: Boolean = false
)
