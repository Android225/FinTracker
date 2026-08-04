package com.example.fintracker.domain.repository

import com.example.fintracker.domain.settings.AppCurrency
import com.example.fintracker.domain.settings.AppLanguage
import com.example.fintracker.domain.settings.AppSettings
import com.example.fintracker.domain.settings.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    val pinConfigured: Flow<Boolean>

    suspend fun setTheme(theme: AppTheme)
    suspend fun setLanguage(language: AppLanguage)
    suspend fun setCurrency(currency: AppCurrency)
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun savePin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
}
