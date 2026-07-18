package com.example.fintracker.presentation.screens.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.R
import com.example.fintracker.domain.AppError
import com.example.fintracker.presentation.common.UiState
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.screens.analytics.components.AnalyticsDonutChart
import com.example.fintracker.presentation.screens.analytics.components.AnalyticsFilterRow
import com.example.fintracker.presentation.screens.analytics.components.AnalyticsLegend
import com.example.fintracker.presentation.screens.analytics.components.AnalyticsTopBar
import com.example.fintracker.presentation.screens.analytics.components.AnalyticsTransactionItem
import com.example.fintracker.presentation.screens.analytics.components.toAnalyticsChartSegments
import com.example.fintracker.presentation.screens.analytics.components.sheets.AnalyticsSheetHost
import com.example.fintracker.presentation.screens.analytics.domain.AnalyticsSheet
import com.example.fintracker.presentation.screens.common.EmptyContent
import com.example.fintracker.presentation.screens.common.ErrorContent
import com.example.fintracker.presentation.screens.common.LoadingContent
import com.example.fintracker.presentation.theme.FinTrackerTheme
import java.time.LocalDate

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel
) {
    val navController = LocalNavController.current
    val state by viewModel.uiState.collectAsState()

    AnalyticsScreenContent(
        state = state,
        onBackClick = { navController.popBackStack() },
        onEvent = viewModel::onEvent
    )
}

@Composable
fun AnalyticsScreenContent(
    state: UiState<AnalyticsContent>,
    onBackClick: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit
) {
    var sheet by remember { mutableStateOf<AnalyticsSheet?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AnalyticsTopBar(onBackClick = onBackClick) }
    ) { innerPadding ->
        when (state) {
            UiState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            UiState.Empty -> EmptyContent(
                text = "Данных для аналитики пока нет",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is UiState.Error -> ErrorContent(
                error = state.error,
                onRetryClick = { onEvent(AnalyticsEvent.Refresh) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is UiState.Content -> {
                AnalyticsContent(
                    content = state.data,
                    modifier = Modifier.padding(innerPadding),
                    onDonutClick = { sheet = AnalyticsSheet.Detail },
                    onFilterClick = { sheet = it }
                )

                sheet?.let { selectedSheet ->
                    AnalyticsSheetHost(
                        sheet = selectedSheet,
                        content = state.data,
                        onEvent = onEvent,
                        onSheetChange = { sheet = it },
                        onDismiss = { sheet = null }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsContent(
    content: AnalyticsContent,
    onDonutClick: () -> Unit,
    onFilterClick: (AnalyticsSheet) -> Unit,
    modifier: Modifier = Modifier
) {
    val chartSegments = remember(content.breakdown) {
        content.breakdown.toAnalyticsChartSegments()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnalyticsDonutChart(
                    totalAmount = content.totalAmount,
                    segments = chartSegments,
                    onClick = onDonutClick
                )
                Spacer(modifier = Modifier.height(24.dp))
                AnalyticsLegend(segments = chartSegments)
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                AnalyticsFilterRow(
                    title = "Тип",
                    value = content.filter.type,
                    iconRes = R.drawable.analytics_filter_type,
                    showDivider = true,
                    onClick = { onFilterClick(AnalyticsSheet.Type) }
                )
                AnalyticsFilterRow(
                    title = "Период",
                    value = content.filter.period,
                    iconRes = R.drawable.analytics_filter_period,
                    showDivider = true,
                    onClick = { onFilterClick(AnalyticsSheet.Period) }
                )
                AnalyticsFilterRow(
                    title = "Статьи",
                    value = content.filter.categories,
                    iconRes = R.drawable.analytics_filter_category,
                    showDivider = true,
                    onClick = { onFilterClick(AnalyticsSheet.Categories) }
                )
                AnalyticsFilterRow(
                    title = "Счёт",
                    value = content.filter.account,
                    iconRes = R.drawable.analytics_filter_account,
                    showDivider = false,
                    onClick = { onFilterClick(AnalyticsSheet.Account) }
                )
            }
        }
        item {
            Text(
                text = "Транзакции",
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (content.transactions.isEmpty()) {
            item {
                Text(
                    text = "Нет транзакций по выбранным фильтрам",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            itemsIndexed(
                items = content.transactions,
                key = { _, transaction -> transaction.id }
            ) { index, transaction ->
                AnalyticsTransactionItem(
                    transaction = transaction,
                    showDivider = index != content.transactions.lastIndex
                )
            }
        }
    }
}

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
                    typeOptions = AnalyticsTransactionType.entries,
                    periodOptions = emptyList(),
                    categoryOptions = emptyList(),
                    accountOptions = emptyList(),
                    selectedType = AnalyticsTransactionType.Expense,
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
