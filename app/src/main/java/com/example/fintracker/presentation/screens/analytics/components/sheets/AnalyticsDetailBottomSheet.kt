package com.example.fintracker.presentation.screens.analytics.components.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.presentation.screens.analytics.AnalyticsBreakdownUi
import com.example.fintracker.presentation.screens.analytics.components.AnalyticsBreakdownList
import com.example.fintracker.presentation.screens.analytics.components.AnalyticsDonutChart
import com.example.fintracker.presentation.screens.analytics.components.toAnalyticsChartSegments

@Composable
fun AnalyticsDetailBottomSheet(
    totalAmount: String,
    breakdown: List<AnalyticsBreakdownUi>,
    modifier: Modifier = Modifier
) {
    val chartSegments = remember(breakdown) {
        breakdown.toAnalyticsChartSegments()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(525.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Детализация",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        AnalyticsDonutChart(
            totalAmount = totalAmount,
            segments = chartSegments,
            size = 240.dp,
            strokeWidth = 24.dp
        )
        Spacer(modifier = Modifier.height(32.dp))
        AnalyticsBreakdownList(segments = chartSegments)
    }
}
