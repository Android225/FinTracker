package com.example.fintracker.presentation.settings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fintracker.R
import com.example.fintracker.domain.settings.AppTheme
import com.example.fintracker.presentation.settings.components.PinInputTestTag
import com.example.fintracker.presentation.settings.components.PinSettingsSheet
import com.example.fintracker.presentation.settings.components.ThemeSheet
import com.example.fintracker.presentation.theme.FinTrackerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun selectingDarkThemeEmitsNewTheme() {
        var selectedTheme = AppTheme.Light
        composeRule.setContent {
            FinTrackerTheme {
                ThemeSheet(
                    selected = selectedTheme,
                    onSelect = { selectedTheme = it }
                )
            }
        }

        val darkTitle = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.theme_dark)
        composeRule.onNodeWithText(darkTitle).performClick()

        composeRule.runOnIdle {
            assertEquals(AppTheme.Dark, selectedTheme)
        }
    }

    @Test
    fun fourPinDigitsAreReturnedToViewModelBoundary() {
        var completedPin: String? = null
        composeRule.setContent {
            FinTrackerTheme {
                PinSettingsSheet(
                    pinConfigured = false,
                    saving = false,
                    onPinComplete = { completedPin = it }
                )
            }
        }

        composeRule.onNodeWithTag(PinInputTestTag).performTextInput("4826")
        composeRule.waitUntil { completedPin != null }

        composeRule.runOnIdle {
            assertEquals("4826", completedPin)
        }
    }
}
