package com.example.fintracker.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fintracker.R
import com.example.fintracker.presentation.settings.SettingsSheet

@Composable
fun SettingsMainSheet(
    pinConfigured: Boolean,
    onOpenSheet: (SettingsSheet) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 680.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsSheetTitle(stringResource(R.string.settings_title))
        SettingsSection(stringResource(R.string.settings_wallet_section)) {
            SettingsRow(
                title = stringResource(R.string.settings_currency),
                icon = R.drawable.settings_currency,
                onClick = { onOpenSheet(SettingsSheet.Currency) }
            )
            SettingsRow(
                title = stringResource(R.string.settings_categories),
                icon = R.drawable.settings_categories,
                onClick = { onOpenSheet(SettingsSheet.Categories) }
            )
        }
        SettingsSection(stringResource(R.string.settings_interface_section)) {
            SettingsRow(
                title = stringResource(R.string.settings_theme),
                icon = R.drawable.settings_theme,
                onClick = { onOpenSheet(SettingsSheet.Theme) }
            )
            SettingsRow(
                title = stringResource(R.string.settings_language),
                icon = R.drawable.settings_language,
                onClick = { onOpenSheet(SettingsSheet.Language) }
            )
        }
        SettingsSection(stringResource(R.string.settings_security_section)) {
            SettingsRow(
                title = stringResource(R.string.settings_pin),
                icon = R.drawable.settings_pin,
                supportingText = if (pinConfigured) {
                    stringResource(R.string.pin_change_title)
                } else {
                    null
                },
                onClick = { onOpenSheet(SettingsSheet.Pin) }
            )
            SettingsRow(
                title = stringResource(R.string.settings_biometrics),
                icon = R.drawable.settings_biometric,
                onClick = { onOpenSheet(SettingsSheet.Biometrics) }
            )
        }
    }
}
