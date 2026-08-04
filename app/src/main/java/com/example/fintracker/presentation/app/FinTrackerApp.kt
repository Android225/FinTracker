package com.example.fintracker.presentation.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.fintracker.R
import com.example.fintracker.domain.settings.AppTheme
import com.example.fintracker.presentation.auth.BiometricLauncher
import com.example.fintracker.presentation.auth.UnlockScreen
import com.example.fintracker.presentation.auth.isBiometricAvailable
import com.example.fintracker.presentation.auth.rememberBiometricLauncher
import com.example.fintracker.presentation.navigation.AppNavGraph
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.screens.splash.SplashScreen
import com.example.fintracker.presentation.settings.SettingsSheetHost
import com.example.fintracker.presentation.settings.SettingsViewModel
import com.example.fintracker.presentation.theme.FinTrackerTheme

private enum class BiometricPurpose {
    Unlock,
    EnableSetting
}

@Composable
fun FinTrackerApp(
    appViewModel: AppViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val appState by appViewModel.uiState.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var biometricPurpose by remember { mutableStateOf<BiometricPurpose?>(null) }
    val currentPurpose = rememberUpdatedState(biometricPurpose)
    val context = LocalContext.current
    val biometricAvailable = remember(context) { isBiometricAvailable(context) }
    val promptTitle = stringResource(R.string.biometric_prompt_title)
    val promptSubtitle = stringResource(R.string.biometric_prompt_subtitle)
    val promptCancel = stringResource(R.string.biometric_prompt_cancel)

    ApplyApplicationLocale(appState.settings.language.languageTag)

    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (appState.settings.theme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.System -> systemDark
    }

    val biometricLauncher = rememberBiometricLauncher(
        onSuccess = {
            when (currentPurpose.value) {
                BiometricPurpose.Unlock -> appViewModel.unlockWithBiometrics()
                BiometricPurpose.EnableSetting -> settingsViewModel.setBiometricEnabled(true)
                null -> Unit
            }
            biometricPurpose = null
        },
        onError = { biometricPurpose = null }
    )

    fun launchBiometrics(purpose: BiometricPurpose) {
        biometricPurpose = purpose
        biometricLauncher.authenticate(promptTitle, promptSubtitle, promptCancel)
    }

    LaunchedEffect(
        showSplash,
        appState.requiresUnlock,
        appState.settings.biometricEnabled,
        biometricAvailable
    ) {
        if (!showSplash && appState.requiresUnlock &&
            appState.settings.biometricEnabled && biometricAvailable
        ) {
            launchBiometrics(BiometricPurpose.Unlock)
        }
    }

    FinTrackerTheme(darkTheme = darkTheme) {
        when {
            showSplash -> SplashScreen(onSplashFinished = { showSplash = false })
            appState.loading -> AppLoadingContent()
            appState.requiresUnlock -> UnlockScreen(
                verifyingPin = appState.verifyingPin,
                pinRejected = appState.pinRejected,
                biometricAvailable = appState.settings.biometricEnabled && biometricAvailable,
                onPinChange = appViewModel::clearPinError,
                onPinComplete = appViewModel::verifyPin,
                onBiometricClick = { launchBiometrics(BiometricPurpose.Unlock) }
            )
            else -> {
                val navController = rememberNavController()
                CompositionLocalProvider(LocalNavController provides navController) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AppNavGraph(onSettingsClick = settingsViewModel::open)
                    }
                    SettingsSheetHost(
                        state = settingsState,
                        biometricAvailable = biometricAvailable,
                        onClose = settingsViewModel::close,
                        onBackToMain = settingsViewModel::backToMain,
                        onOpenSheet = settingsViewModel::openSheet,
                        onThemeSelect = settingsViewModel::setTheme,
                        onLanguageSelect = settingsViewModel::setLanguage,
                        onCurrencySelect = settingsViewModel::setCurrency,
                        onCategoryQueryChange = settingsViewModel::setCategoryQuery,
                        onPinComplete = settingsViewModel::savePin,
                        onBiometricToggle = { enabled ->
                            if (enabled) launchBiometrics(BiometricPurpose.EnableSetting)
                            else settingsViewModel.setBiometricEnabled(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplyApplicationLocale(languageTag: String) {
    LaunchedEffect(languageTag) {
        val requested = LocaleListCompat.forLanguageTags(languageTag)
        if (AppCompatDelegate.getApplicationLocales() != requested) {
            AppCompatDelegate.setApplicationLocales(requested)
        }
    }
}

@Composable
private fun AppLoadingContent() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
