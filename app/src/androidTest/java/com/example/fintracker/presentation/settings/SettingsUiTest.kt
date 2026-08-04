package com.example.fintracker.presentation.settings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.swipe
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fintracker.R
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.settings.AppTheme
import com.example.fintracker.presentation.settings.components.CategoriesListTestTag
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

    @Test
    fun categoriesSheetCanBeDismissedByDraggingListDown() {
        var dismissed = false
        val categories = (1..20).map { index ->
            Category(index, "Category $index", "📌", isIncome = false)
        }
        composeRule.setContent {
            FinTrackerTheme {
                SettingsSheetHost(
                    state = SettingsUiState(
                        sheet = SettingsSheet.Categories,
                        categories = categories
                    ),
                    biometricAvailable = false,
                    onClose = {},
                    onBackToMain = { dismissed = true },
                    onOpenSheet = {},
                    onThemeSelect = {},
                    onLanguageSelect = {},
                    onCurrencySelect = {},
                    onCategoryQueryChange = {},
                    onPinComplete = {},
                    onBiometricToggle = {}
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(CategoriesListTestTag).performTouchInput {
            swipe(
                start = center,
                end = center.copy(y = height * 0.9f),
                durationMillis = 400
            )
        }

        composeRule.waitUntil(timeoutMillis = 3_000) { dismissed }
    }
}
