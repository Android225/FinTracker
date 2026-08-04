package com.example.fintracker.presentation.screens.analytics.components.sheets.daterange

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.R
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.presentation.screens.analytics.components.sheets.AnalyticsOutline
import com.example.fintracker.presentation.screens.analytics.components.sheets.AnalyticsPrimary
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AnalyticsDateRangeBottomSheet(
    initialPeriod: AnalyticsPeriod,
    onCancel: () -> Unit,
    onApply: (AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    var period by remember(initialPeriod) { mutableStateOf(initialPeriod) }
    var selectingEnd by remember(initialPeriod) { mutableStateOf(false) }
    var visibleMonth by remember(initialPeriod) {
        mutableStateOf(YearMonth.from(initialPeriod.start))
    }

    val selectDate: (LocalDate) -> Unit = { date ->
        if (!selectingEnd) {
            period = AnalyticsPeriod(date, date)
            selectingEnd = true
        } else {
            period = if (date.isBefore(period.start)) {
                AnalyticsPeriod(date, period.start)
            } else {
                AnalyticsPeriod(period.start, date)
            }
            selectingEnd = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(698.dp)
    ) {
        Text(
            text = stringResource(R.string.any_period),
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
            fontSize = 20.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateChip(
                text = period.start.formatForChip(locale),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .width(12.dp)
                    .height(1.dp)
                    .background(AnalyticsOutline)
            )
            DateChip(
                text = period.end.formatForChip(locale),
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                CalendarMonth(
                    month = visibleMonth,
                    period = period,
                    showNavigation = true,
                    onPreviousMonth = { visibleMonth = visibleMonth.minusMonths(1) },
                    onNextMonth = { visibleMonth = visibleMonth.plusMonths(1) },
                    onDateClick = selectDate
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CalendarMonth(
                    month = visibleMonth.plusMonths(1),
                    period = period,
                    showNavigation = false,
                    onPreviousMonth = {},
                    onNextMonth = {},
                    onDateClick = selectDate
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(R.string.cancel),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AnalyticsPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { onApply(period) },
                modifier = Modifier.height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnalyticsPrimary)
            ) {
                Text(
                    text = stringResource(R.string.apply),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DateChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .border(1.dp, AnalyticsOutline, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

private fun LocalDate.formatForChip(locale: Locale): String =
    format(DateTimeFormatter.ofPattern("d MMM yyyy", locale))
