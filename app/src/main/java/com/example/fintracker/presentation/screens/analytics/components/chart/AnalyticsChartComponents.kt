package com.example.fintracker.presentation.screens.analytics.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun AnalyticsDonutChart(
    totalAmount: String,
    segments: List<AnalyticsChartSegment>,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    strokeWidth: Dp = 22.dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(size.toPx() - strokePx, size.toPx() - strokePx)
            val topLeft = Offset(strokePx / 2, strokePx / 2)
            val sweepAngles = segments.calculateSweepAngles()
            var startAngle = -90f

            if (segments.isEmpty()) {
                drawArc(
                    color = Color(0xFFC4C7C5),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                )
            } else {
                segments.zip(sweepAngles).forEach { (segment, sweep) ->
                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                    )
                    startAngle += sweep
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Всего за период",
                fontSize = if (size > 220.dp) 16.sp else 14.sp,
                lineHeight = if (size > 220.dp) 19.sp else 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = totalAmount,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = if (size > 220.dp) 32.sp else 28.sp,
                lineHeight = if (size > 220.dp) 38.sp else 33.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
internal fun AnalyticsLegend(
    segments: List<AnalyticsChartSegment>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        segments.take(MAX_LEGEND_SEGMENTS).chunked(LEGEND_COLUMNS).forEach { rowSegments ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(rowSegments.size.toFloat() / LEGEND_COLUMNS)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowSegments.forEach { segment ->
                    AnalyticsLegendItem(
                        segment = segment,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsLegendItem(
    segment: AnalyticsChartSegment,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.heightIn(min = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        val itemMaxWidth = maxWidth
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(segment.color, CircleShape)
            )
            Text(
                text = segment.breakdown.title,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .widthIn(max = itemMaxWidth - 18.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun AnalyticsBreakdownList(
    segments: List<AnalyticsChartSegment>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        segments.forEach { segment ->
            AnalyticsBreakdownItem(segment = segment)
        }
        if (segments.isEmpty()) {
            Text(
                text = "Нет данных за выбранный период",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun AnalyticsBreakdownItem(segment: AnalyticsChartSegment) {
    val item = segment.breakdown
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(segment.color, CircleShape)
                )
                Text(
                    text = item.title,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = item.amount,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "(${item.percent}%)",
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 14.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFFC4C7C5), RoundedCornerShape(100.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((item.percent / 100f).coerceIn(0f, 1f))
                    .height(6.dp)
                    .background(segment.color, RoundedCornerShape(100.dp))
            )
        }
    }
}

private const val MAX_LEGEND_SEGMENTS = 5
private const val LEGEND_COLUMNS = 3
