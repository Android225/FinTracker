package com.example.fintracker.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fintracker.di.qualifier.SettingsPreferences
import com.example.fintracker.domain.settings.AppCurrency
import com.example.fintracker.domain.settings.AppLanguage
import com.example.fintracker.domain.settings.AppSettings
import com.example.fintracker.domain.settings.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsLocalDataSource @Inject constructor(
    @param:SettingsPreferences private val dataStore: DataStore<Preferences>
) {
    val settings: Flow<AppSettings> = dataStore.data.map { preferences ->
        AppSettings(
            theme = preferences[ThemeKey].toEnumOrDefault(AppTheme.System),
            language = preferences[LanguageKey].toEnumOrDefault(AppLanguage.Russian),
            currency = preferences[CurrencyKey].toEnumOrDefault(AppCurrency.Rub),
            biometricEnabled = preferences[BiometricKey] ?: false
        )
    }

    suspend fun setTheme(theme: AppTheme) = update(ThemeKey, theme.name)
    suspend fun setLanguage(language: AppLanguage) = update(LanguageKey, language.name)
    suspend fun setCurrency(currency: AppCurrency) = update(CurrencyKey, currency.name)

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[BiometricKey] = enabled }
    }

    private suspend fun update(key: Preferences.Key<String>, value: String) {
        dataStore.edit { it[key] = value }
    }

    private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T =
        enumValues<T>().firstOrNull { it.name == this } ?: default

    private companion object {
        val ThemeKey = stringPreferencesKey("theme")
        val LanguageKey = stringPreferencesKey("language")
        val CurrencyKey = stringPreferencesKey("currency")
        val BiometricKey = booleanPreferencesKey("biometric_enabled")
    }
}
