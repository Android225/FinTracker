package com.example.fintracker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.repository.SettingsRepository
import com.example.fintracker.domain.settings.AppCurrency
import com.example.fintracker.domain.settings.AppLanguage
import com.example.fintracker.domain.settings.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    categoriesRepository: CategoriesRepository
) : ViewModel() {
    private val activeSheet = MutableStateFlow(SettingsSheet.Closed)
    private val categoryQuery = MutableStateFlow("")
    private val savingPin = MutableStateFlow(false)

    private val localState = combine(
        activeSheet,
        categoryQuery,
        savingPin
    ) { sheet, query, saving -> LocalState(sheet, query, saving) }

    val state: StateFlow<SettingsUiState> = combine(
        settingsRepository.settings,
        settingsRepository.pinConfigured,
        categoriesRepository.observeCategories(),
        localState
    ) { settings, pinConfigured, categories, local ->
        SettingsUiState(
            settings = settings,
            sheet = local.sheet,
            pinConfigured = pinConfigured,
            categories = categories.filter {
                local.query.isBlank() || it.name.contains(local.query, ignoreCase = true)
            },
            categoryQuery = local.query,
            savingPin = local.savingPin
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun open() {
        activeSheet.value = SettingsSheet.Main
    }

    fun close() {
        activeSheet.value = SettingsSheet.Closed
        categoryQuery.value = ""
    }

    fun openSheet(sheet: SettingsSheet) {
        activeSheet.value = sheet
    }

    fun backToMain() {
        activeSheet.value = SettingsSheet.Main
        categoryQuery.value = ""
    }

    fun setTheme(theme: AppTheme) = updateSetting {
        settingsRepository.setTheme(theme)
        backToMain()
    }

    fun setLanguage(language: AppLanguage) = updateSetting {
        settingsRepository.setLanguage(language)
        backToMain()
    }

    fun setCurrency(currency: AppCurrency) = updateSetting {
        settingsRepository.setCurrency(currency)
        backToMain()
    }

    fun setBiometricEnabled(enabled: Boolean) = updateSetting {
        settingsRepository.setBiometricEnabled(enabled)
    }

    fun setCategoryQuery(query: String) {
        categoryQuery.value = query
    }

    fun savePin(pin: String) {
        if (pin.length != PinLength || savingPin.value) return
        savingPin.value = true
        viewModelScope.launch {
            try {
                settingsRepository.savePin(pin)
                backToMain()
            } finally {
                savingPin.value = false
            }
        }
    }

    private fun updateSetting(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    private companion object {
        const val PinLength = 4
    }

    private data class LocalState(
        val sheet: SettingsSheet,
        val query: String,
        val savingPin: Boolean
    )
}
