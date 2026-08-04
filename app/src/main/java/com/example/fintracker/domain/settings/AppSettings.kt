package com.example.fintracker.domain.settings

data class AppSettings(
    val theme: AppTheme = AppTheme.System,
    val language: AppLanguage = AppLanguage.Russian,
    val currency: AppCurrency = AppCurrency.Rub,
    val biometricEnabled: Boolean = false
)
