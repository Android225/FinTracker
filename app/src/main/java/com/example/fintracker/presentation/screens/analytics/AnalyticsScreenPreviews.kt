package com.example.fintracker.presentation.screens.analytics

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.theme.FinTrackerTheme
import java.time.LocalDate

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AnalyticsScreenPreview() {
    val period = AnalyticsPeriod(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 16))
    FinTrackerTheme {
        AnalyticsScreenContent(
            state = UiState.Content(
                AnalyticsContent(
                    totalAmount = "132 244 ₽",
                    filter = AnalyticsFilterUi(
                        type = "Расходы",
                        period = "01.07.2026 – 16.07.2026",
                        categories = "Ремонт, Авто",
                        account = "Все счета"
                    ),
                    breakdown = listOf(
                        AnalyticsBreakdownUi(1, "Ремонт", "80 200 ₽", 61),
                        AnalyticsBreakdownUi(2, "Авто", "31 500 ₽", 24),
                        AnalyticsBreakdownUi(3, "Другое", "20 544 ₽", 15)
                    ),
                    transactions = listOf(
                        AnalyticsTransactionUi(1, "🐶", "На собачку", "Наличные", "123 322 ₽"),
                        AnalyticsTransactionUi(2, "🛒", "Покупки в магазине", "Сбербанк", "45 100 ₽"),
                        AnalyticsTransactionUi(3, "🏢", "Ремонт квартиры", "Тинькофф", "890 000 ₽")
                    ),
                    typeOptions = TransactionType.entries,
                    periodOptions = emptyList(),
                    categoryOptions = emptyList(),
                    accountOptions = emptyList(),
                    selectedType = TransactionType.Expense,
                    selectedPeriod = period
                )
            ),
            onBackClick = {},
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsScreenLoadingPreview() {
    FinTrackerTheme {
        AnalyticsScreenContent(
            state = UiState.Loading,
            onBackClick = {},
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsScreenErrorPreview() {
    FinTrackerTheme {
        AnalyticsScreenContent(
            state = UiState.Error(AppError.Network),
            onBackClick = {},
            onEvent = {}
        )
    }
}
