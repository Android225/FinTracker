package com.example.fintracker.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.fintracker.presentation.common.components.bottomsheet.FinTrackerBottomSheet
import com.example.fintracker.presentation.settings.components.BiometricSettingsSheet
import com.example.fintracker.presentation.settings.components.CategoriesSheet
import com.example.fintracker.presentation.settings.components.CurrencySheet
import com.example.fintracker.presentation.settings.components.LanguageSheet
import com.example.fintracker.presentation.settings.components.PinSettingsSheet
import com.example.fintracker.presentation.settings.components.SettingsMainSheet
import com.example.fintracker.presentation.settings.components.ThemeSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheetHost(
    state: SettingsUiState,
    biometricAvailable: Boolean,
    onClose: () -> Unit,
    onBackToMain: () -> Unit,
    onOpenSheet: (SettingsSheet) -> Unit,
    onThemeSelect: (com.example.fintracker.domain.settings.AppTheme) -> Unit,
    onLanguageSelect: (com.example.fintracker.domain.settings.AppLanguage) -> Unit,
    onCurrencySelect: (com.example.fintracker.domain.settings.AppCurrency) -> Unit,
    onCategoryQueryChange: (String) -> Unit,
    onPinComplete: (String) -> Unit,
    onBiometricToggle: (Boolean) -> Unit
) {
    if (state.sheet == SettingsSheet.Closed) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dismiss = if (state.sheet == SettingsSheet.Main) onClose else onBackToMain
    BackHandler(onBack = dismiss)

    FinTrackerBottomSheet(
        onDismissRequest = dismiss,
        sheetState = sheetState,
        dragHandleHeight = 24.dp
    ) {
        when (state.sheet) {
            SettingsSheet.Main -> SettingsMainSheet(
                pinConfigured = state.pinConfigured,
                onOpenSheet = onOpenSheet
            )
            SettingsSheet.Currency -> CurrencySheet(
                selected = state.settings.currency,
                onSelect = onCurrencySelect
            )
            SettingsSheet.Categories -> CategoriesSheet(
                categories = state.categories,
                query = state.categoryQuery,
                onQueryChange = onCategoryQueryChange
            )
            SettingsSheet.Theme -> ThemeSheet(
                selected = state.settings.theme,
                onSelect = onThemeSelect
            )
            SettingsSheet.Language -> LanguageSheet(
                selected = state.settings.language,
                onSelect = onLanguageSelect
            )
            SettingsSheet.Pin -> PinSettingsSheet(
                pinConfigured = state.pinConfigured,
                saving = state.savingPin,
                onPinComplete = onPinComplete
            )
            SettingsSheet.Biometrics -> BiometricSettingsSheet(
                enabled = state.settings.biometricEnabled,
                pinConfigured = state.pinConfigured,
                biometricAvailable = biometricAvailable,
                onToggle = onBiometricToggle
            )
            SettingsSheet.Closed -> Unit
        }
    }
}
