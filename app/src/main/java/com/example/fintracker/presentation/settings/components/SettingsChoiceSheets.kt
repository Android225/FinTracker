package com.example.fintracker.presentation.settings.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fintracker.R
import com.example.fintracker.domain.settings.AppCurrency
import com.example.fintracker.domain.settings.AppLanguage
import com.example.fintracker.domain.settings.AppTheme

@Composable
fun CurrencySheet(
    selected: AppCurrency,
    onSelect: (AppCurrency) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        SettingsSheetTitle(stringResource(R.string.settings_currency))
        CurrencyOption.entries.forEach { option ->
            SettingsChoiceRow(
                title = stringResource(option.title),
                supportingText = option.currency.code,
                leadingText = option.flag,
                selected = option.currency == selected,
                onClick = { onSelect(option.currency) }
            )
        }
    }
}

@Composable
fun LanguageSheet(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        SettingsSheetTitle(stringResource(R.string.settings_language))
        LanguageOption.entries.forEach { option ->
            SettingsChoiceRow(
                title = stringResource(option.title),
                leadingText = option.flag,
                selected = option.language == selected,
                onClick = { onSelect(option.language) }
            )
        }
    }
}

@Composable
fun ThemeSheet(
    selected: AppTheme,
    onSelect: (AppTheme) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        SettingsSheetTitle(stringResource(R.string.settings_theme))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeOption.entries.forEach { option ->
                ThemeCard(
                    option = option,
                    selected = option.theme == selected,
                    onClick = { onSelect(option.theme) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    option: ThemeOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .background(option.previewBrush(), RoundedCornerShape(10.dp))
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(option.icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(option.title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

private fun ThemeOption.previewBrush(): Brush = when (theme) {
    AppTheme.Light -> Brush.verticalGradient(listOf(Color.White, Color(0xFFF7F3F8)))
    AppTheme.Dark -> Brush.verticalGradient(listOf(Color(0xFF25232A), Color(0xFF151318)))
    AppTheme.System -> Brush.verticalGradient(listOf(Color.White, Color(0xFF17151A)))
}

private enum class CurrencyOption(
    val currency: AppCurrency,
    val flag: String,
    @param:StringRes val title: Int
) {
    Rub(AppCurrency.Rub, "🇷🇺", R.string.currency_rub),
    Usd(AppCurrency.Usd, "🇺🇸", R.string.currency_usd),
    Eur(AppCurrency.Eur, "🇪🇺", R.string.currency_eur),
    Gbp(AppCurrency.Gbp, "🇬🇧", R.string.currency_gbp),
    Cny(AppCurrency.Cny, "🇨🇳", R.string.currency_cny)
}

private enum class LanguageOption(
    val language: AppLanguage,
    val flag: String,
    @param:StringRes val title: Int
) {
    Russian(AppLanguage.Russian, "🇷🇺", R.string.language_russian),
    English(AppLanguage.English, "🇬🇧", R.string.language_english),
    German(AppLanguage.German, "🇩🇪", R.string.language_german),
    French(AppLanguage.French, "🇫🇷", R.string.language_french),
    Spanish(AppLanguage.Spanish, "🇪🇸", R.string.language_spanish)
}

private enum class ThemeOption(
    val theme: AppTheme,
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int
) {
    Light(AppTheme.Light, R.string.theme_light, R.drawable.theme_sun),
    Dark(AppTheme.Dark, R.string.theme_dark, R.drawable.settings_theme),
    System(AppTheme.System, R.string.theme_system, R.drawable.theme_system)
}
