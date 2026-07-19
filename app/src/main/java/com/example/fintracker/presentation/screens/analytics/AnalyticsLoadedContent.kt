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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.R
import com.example.fintracker.presentation.screens.analytics.components.chart.AnalyticsDonutChart
import com.example.fintracker.presentation.screens.analytics.components.chart.AnalyticsLegend
import com.example.fintracker.presentation.screens.analytics.components.chart.toOverviewChartSegments
import com.example.fintracker.presentation.screens.analytics.components.filters.AnalyticsFilterRow
import com.example.fintracker.presentation.screens.analytics.components.transactions.AnalyticsTransactionItem

@Composable
internal fun AnalyticsLoadedContent(
    content: AnalyticsContent,
    onDonutClick: () -> Unit,
    onFilterClick: (AnalyticsSheet) -> Unit,
    modifier: Modifier = Modifier
) {
    val chartSegments = remember(content.breakdown) {
        content.breakdown.toOverviewChartSegments()
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
