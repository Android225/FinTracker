package com.example.fintracker.presentation.common.model

import com.example.fintracker.domain.error.AppError

sealed interface ScreenEffect {
    data class ShowError(val error: AppError) : ScreenEffect
}
