package com.example.fintracker.presentation.settings

import com.example.fintracker.domain.Category
import com.example.fintracker.domain.settings.AppSettings

enum class SettingsSheet {
    Closed,
    Main,
    Currency,
    Categories,
    Theme,
    Language,
    Pin,
    Biometrics
}

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val sheet: SettingsSheet = SettingsSheet.Closed,
    val pinConfigured: Boolean = false,
    val categories: List<Category> = emptyList(),
    val categoryQuery: String = "",
    val savingPin: Boolean = false
)
