package com.example.fintracker.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    primaryContainer = PrimaryContainer,     // #EADDFF — FAB кнопка
    onPrimaryContainer = OnPrimaryContainer, // #21005D — иконка "+" внутри FAB
    secondaryContainer = SecondaryContainer, // #E8DEF8 — активный таб навигации
    onSecondaryContainer = OnSecondaryContainer, // #1D192B — штрих активной иконки
    surfaceContainer = SurfaceContainer.copy(alpha = 0.1f),                       // #F3EDF7 — фон TopAppBar / BottomNav
    surfaceVariant = SurfaceContainer,                // #F3EDF7 — фон баров
    onSurface = OnSurface,                   // #1D1B20 — весь текст
    onSurfaceVariant = OnSurfaceVariant,     // #49454F — второстепенные подписи
    outline = OutlineVariant,                // #C4C7C5 — границы иконок
    outlineVariant = OutlineVariant,
    background = BackgroundColor

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FinTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
