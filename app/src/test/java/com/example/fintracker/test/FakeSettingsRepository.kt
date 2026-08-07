package com.example.fintracker.test

import com.example.fintracker.domain.repository.SettingsRepository
import com.example.fintracker.domain.settings.AppCurrency
import com.example.fintracker.domain.settings.AppLanguage
import com.example.fintracker.domain.settings.AppSettings
import com.example.fintracker.domain.settings.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository(
    initialSettings: AppSettings = AppSettings(),
    initialPin: String? = null
) : SettingsRepository {
    private val mutableSettings = MutableStateFlow(initialSettings)
    private val mutablePinConfigured = MutableStateFlow(initialPin != null)
    var savedPin: String? = initialPin
        private set

    override val settings: Flow<AppSettings> = mutableSettings
    override val pinConfigured: Flow<Boolean> = mutablePinConfigured

    override suspend fun setTheme(theme: AppTheme) = update { copy(theme = theme) }
    override suspend fun setLanguage(language: AppLanguage) = update { copy(language = language) }
    override suspend fun setCurrency(currency: AppCurrency) = update { copy(currency = currency) }

    override suspend fun setBiometricEnabled(enabled: Boolean) =
        update { copy(biometricEnabled = enabled) }

    override suspend fun savePin(pin: String) {
        savedPin = pin
        mutablePinConfigured.value = true
    }

    override suspend fun verifyPin(pin: String): Boolean = pin == savedPin

    private fun update(transform: AppSettings.() -> AppSettings) {
        mutableSettings.value = mutableSettings.value.transform()
    }
}
