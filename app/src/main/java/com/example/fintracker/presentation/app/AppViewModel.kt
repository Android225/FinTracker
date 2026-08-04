package com.example.fintracker.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.domain.repository.SettingsRepository
import com.example.fintracker.domain.settings.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppUiState(
    val loading: Boolean = true,
    val settings: AppSettings = AppSettings(),
    val pinConfigured: Boolean = false,
    val sessionUnlocked: Boolean = false,
    val verifyingPin: Boolean = false,
    val pinRejected: Boolean = false
) {
    val requiresUnlock: Boolean
        get() = !loading && pinConfigured && !sessionUnlocked
}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val sessionUnlocked = MutableStateFlow(false)
    private val authLoaded = MutableStateFlow(false)
    private val verifyingPin = MutableStateFlow(false)
    private val pinRejected = MutableStateFlow(false)
    private var firstAuthValue = true

    private val authState = combine(
        sessionUnlocked,
        authLoaded,
        verifyingPin,
        pinRejected
    ) { unlocked, loaded, verifying, rejected ->
        AuthState(unlocked, loaded, verifying, rejected)
    }

    val uiState: StateFlow<AppUiState> = combine(
        settingsRepository.settings,
        settingsRepository.pinConfigured,
        authState
    ) { settings, pinConfigured, auth ->
        AppUiState(
            loading = !auth.loaded,
            settings = settings,
            pinConfigured = pinConfigured,
            sessionUnlocked = auth.unlocked,
            verifyingPin = auth.verifying,
            pinRejected = auth.rejected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppUiState()
    )

    init {
        settingsRepository.pinConfigured
            .onEach { configured ->
                if (firstAuthValue) {
                    sessionUnlocked.value = !configured
                    firstAuthValue = false
                }
                authLoaded.value = true
            }
            .launchIn(viewModelScope)
    }

    fun verifyPin(pin: String) {
        if (pin.length != PinLength || verifyingPin.value) return
        verifyingPin.value = true
        pinRejected.value = false
        viewModelScope.launch {
            val accepted = settingsRepository.verifyPin(pin)
            sessionUnlocked.value = accepted
            pinRejected.value = !accepted
            verifyingPin.value = false
        }
    }

    fun unlockWithBiometrics() {
        sessionUnlocked.value = true
        pinRejected.value = false
    }

    fun clearPinError() {
        pinRejected.value = false
    }

    private companion object {
        const val PinLength = 4
    }

    private data class AuthState(
        val unlocked: Boolean,
        val loaded: Boolean,
        val verifying: Boolean,
        val rejected: Boolean
    )
}
