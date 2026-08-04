package com.example.fintracker.data.repository

import com.example.fintracker.data.settings.AuthLocalDataSource
import com.example.fintracker.data.settings.PinHasher
import com.example.fintracker.data.settings.SettingsLocalDataSource
import com.example.fintracker.domain.repository.SettingsRepository
import com.example.fintracker.domain.settings.AppCurrency
import com.example.fintracker.domain.settings.AppLanguage
import com.example.fintracker.domain.settings.AppSettings
import com.example.fintracker.domain.settings.AppTheme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsSource: SettingsLocalDataSource,
    private val authSource: AuthLocalDataSource,
    private val pinHasher: PinHasher
) : SettingsRepository {
    override val settings: Flow<AppSettings> = settingsSource.settings
    override val pinConfigured: Flow<Boolean> = authSource.pinConfigured

    override suspend fun setTheme(theme: AppTheme) = settingsSource.setTheme(theme)
    override suspend fun setLanguage(language: AppLanguage) = settingsSource.setLanguage(language)
    override suspend fun setCurrency(currency: AppCurrency) = settingsSource.setCurrency(currency)

    override suspend fun setBiometricEnabled(enabled: Boolean) =
        settingsSource.setBiometricEnabled(enabled)

    override suspend fun savePin(pin: String) {
        require(pin.length == PinLength && pin.all(Char::isDigit))
        authSource.savePin(pinHasher.create(pin))
    }

    override suspend fun verifyPin(pin: String): Boolean =
        authSource.readPin()?.let { pinHasher.verify(pin, it) } ?: false

    private companion object {
        const val PinLength = 4
    }
}
