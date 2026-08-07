package com.example.fintracker.presentation.screens.analytics.components.sheets.daterange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.R
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.presentation.screens.analytics.components.sheets.AnalyticsPrimary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun CalendarMonth(
    month: YearMonth,
    period: AnalyticsPeriod,
    showNavigation: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (LocalDate) -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val monthTitle = remember(month, locale) { month.title(locale) }
    val weekdayLabels = remember(locale) { weekdayLabels(locale) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showNavigation) {
                MonthArrow(stringResource(R.string.last_month), onPreviousMonth)
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
            Text(
                text = monthTitle,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (showNavigation) {
                MonthArrow(stringResource(R.string.next_month), onNextMonth, rotate = true)
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp),
                    fontSize = 12.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        month.weeks().forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    CalendarDay(
                        date = date,
                        period = period,
                        onClick = onDateClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthArrow(
    contentDescription: String,
    onClick: () -> Unit,
    rotate: Boolean = false
) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Icon(
            painter = painterResource(R.drawable.analytics_chevron_left),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(24.dp)
                .then(if (rotate) Modifier.rotate(180f) else Modifier),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate?,
    period: AnalyticsPeriod,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    if (date == null) {
        Spacer(modifier = modifier.height(40.dp))
        return
    }

    val isStart = date == period.start
    val isEnd = date == period.end
    val isInRange = date in period.start..period.end
    val isSingleDay = period.start == period.end

    Box(
        modifier = modifier
            .height(40.dp)
            .clickable { onClick(date) },
        contentAlignment = Alignment.Center
    ) {
        if (isInRange && !isSingleDay) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (isStart || isEnd) 0.5f else 1f)
                    .height(40.dp)
                    .align(if (isStart) Alignment.CenterEnd else Alignment.CenterStart)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
        if (isStart || isEnd) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AnalyticsPrimary, CircleShape)
            )
        }
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            color = when {
                isStart || isEnd -> Color.White
                isInRange -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center
        )
    }
}

// Разбивает месяц на календарные недели.
private fun YearMonth.weeks(): List<List<LocalDate?>> {
    val leadingEmptyDays = atDay(1).dayOfWeek.value - 1
    val cells = MutableList<LocalDate?>(leadingEmptyDays) { null }
    repeat(lengthOfMonth()) { dayIndex -> cells += atDay(dayIndex + 1) }
    while (cells.size % 7 != 0) cells += null
    return cells.chunked(7)
}

private fun YearMonth.title(locale: Locale): String {
    val monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
        .replaceFirstChar { character ->
            if (character.isLowerCase()) character.titlecase(locale) else character.toString()
        }
    return "$monthName $year"
}

private fun weekdayLabels(locale: Locale): List<String> = DayOfWeek.entries.map { day ->
    day.getDisplayName(TextStyle.SHORT_STANDALONE, locale)
}
