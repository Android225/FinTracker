package com.example.fintracker.presentation.screens.analytics.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintracker.R
import com.example.fintracker.presentation.screens.analytics.AnalyticsPeriod
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AnalyticsDateRangeBottomSheet(
    initialPeriod: AnalyticsPeriod,
    onCancel: () -> Unit,
    onApply: (AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
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
            text = "Произвольный период",
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
                text = period.start.formatForChip(),
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
                text = period.end.formatForChip(),
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
                    text = "Отмена",
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
                    text = "Применить",
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

@Composable
private fun CalendarMonth(
    month: YearMonth,
    period: AnalyticsPeriod,
    showNavigation: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (LocalDate) -> Unit
) {
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
                MonthArrow(
                    contentDescription = "Предыдущий месяц",
                    onClick = onPreviousMonth
                )
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
            Text(
                text = month.title(),
                fontSize = 16.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (showNavigation) {
                MonthArrow(
                    contentDescription = "Следующий месяц",
                    rotate = true,
                    onClick = onNextMonth
                )
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            WEEKDAY_LABELS.forEach { label ->
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
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
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
                    .fillMaxWidth(
                        when {
                            isStart || isEnd -> 0.5f
                            else -> 1f
                        }
                    )
                    .height(40.dp)
                    .align(if (isStart) Alignment.CenterEnd else Alignment.CenterStart)
                    .background(AnalyticsRange)
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
            color = if (isStart || isEnd) Color.White else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

private fun YearMonth.weeks(): List<List<LocalDate?>> {
    val leadingEmptyDays = atDay(1).dayOfWeek.value - 1
    val cells = MutableList<LocalDate?>(leadingEmptyDays) { null }
    repeat(lengthOfMonth()) { dayIndex -> cells += atDay(dayIndex + 1) }
    while (cells.size % 7 != 0) cells += null
    return cells.chunked(7)
}

private fun YearMonth.title(): String = "${MONTH_NAMES[monthValue - 1]} $year"

private fun LocalDate.formatForChip(): String =
    "$dayOfMonth ${SHORT_MONTH_NAMES[monthValue - 1]} $year"

private val WEEKDAY_LABELS = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
private val MONTH_NAMES = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
)
private val SHORT_MONTH_NAMES = listOf(
    "янв.", "февр.", "мар.", "апр.", "мая", "июн.",
    "июл.", "авг.", "сент.", "окт.", "нояб.", "дек."
)
