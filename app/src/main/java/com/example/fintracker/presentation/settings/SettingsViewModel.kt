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
    private val pinStep = MutableStateFlow(PinSettingsStep.EnterNew)
    private val pinError = MutableStateFlow<PinSettingsError?>(null)
    private var pendingNewPin: String? = null

    private val pinState = combine(pinStep, pinError) { step, error ->
        PinState(step, error)
    }

    private val localState = combine(
        activeSheet,
        categoryQuery,
        savingPin,
        pinState
    ) { sheet, query, saving, pin -> LocalState(sheet, query, saving, pin) }

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
            savingPin = local.savingPin,
            pinStep = local.pin.step,
            pinError = local.pin.error
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
        resetPinFlow()
    }

    fun openSheet(sheet: SettingsSheet) {
        if (sheet == SettingsSheet.Pin) {
            startPinFlow(pinConfigured = state.value.pinConfigured)
        }
        activeSheet.value = sheet
    }

    fun backToMain() {
        activeSheet.value = SettingsSheet.Main
        categoryQuery.value = ""
        resetPinFlow()
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

    fun submitPin(pin: String) {
        if (pin.length != PinLength || savingPin.value) return

        pinError.value = null
        when (pinStep.value) {
            PinSettingsStep.VerifyCurrent -> verifyCurrentPin(pin)
            PinSettingsStep.EnterNew -> {
                pendingNewPin = pin
                pinStep.value = PinSettingsStep.ConfirmNew
            }
            PinSettingsStep.ConfirmNew -> {
                if (pin != pendingNewPin) {
                    pinError.value = PinSettingsError.ConfirmationMismatch
                    return
                }
                persistPin(pin)
            }
        }
    }

    fun clearPinError() {
        pinError.value = null
    }

    private fun verifyCurrentPin(pin: String) {
        savingPin.value = true
        viewModelScope.launch {
            try {
                if (settingsRepository.verifyPin(pin)) {
                    pinStep.value = PinSettingsStep.EnterNew
                } else {
                    pinError.value = PinSettingsError.WrongCurrent
                }
            } finally {
                savingPin.value = false
            }
        }
    }

    private fun persistPin(pin: String) {
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

    private fun startPinFlow(pinConfigured: Boolean) {
        pendingNewPin = null
        pinError.value = null
        pinStep.value = if (pinConfigured) {
            PinSettingsStep.VerifyCurrent
        } else {
            PinSettingsStep.EnterNew
        }
    }

    private fun resetPinFlow() {
        pendingNewPin = null
        pinError.value = null
        pinStep.value = PinSettingsStep.EnterNew
        savingPin.value = false
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
        val savingPin: Boolean,
        val pin: PinState
    )

    private data class PinState(
        val step: PinSettingsStep,
        val error: PinSettingsError?
    )
}
