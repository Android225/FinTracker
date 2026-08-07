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

enum class PinSettingsStep {
    VerifyCurrent,
    EnterNew,
    ConfirmNew
}

enum class PinSettingsError {
    WrongCurrent,
    ConfirmationMismatch
}

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val sheet: SettingsSheet = SettingsSheet.Closed,
    val pinConfigured: Boolean = false,
    val categories: List<Category> = emptyList(),
    val categoryQuery: String = "",
    val savingPin: Boolean = false,
    val pinStep: PinSettingsStep = PinSettingsStep.EnterNew,
    val pinError: PinSettingsError? = null
)
