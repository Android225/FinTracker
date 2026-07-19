package com.example.fintracker.presentation.screens.analytics.components.chart

import androidx.compose.ui.graphics.Color
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsBreakdownUi

internal data class AnalyticsChartSegment(
    val breakdown: AnalyticsBreakdownUi,
    val color: Color
)

// Назначает каждой категории стабильный цвет диаграммы.
internal fun List<AnalyticsBreakdownUi>.toAnalyticsChartSegments(): List<AnalyticsChartSegment> {
    val colorsByCategoryId = distinctBy(AnalyticsBreakdownUi::categoryId)
        .map(AnalyticsBreakdownUi::categoryId)
        .sorted()
        .mapIndexed { index, categoryId -> categoryId to analyticsCategoryColor(index) }
        .toMap()

    return map { breakdown ->
        AnalyticsChartSegment(
            breakdown = breakdown,
            color = checkNotNull(colorsByCategoryId[breakdown.categoryId])
        )
    }
}

private fun analyticsCategoryColor(index: Int): Color {
    chartPalette.getOrNull(index)?.let { return it }

    val generatedIndex = index - chartPalette.size
    val hue = (generatedIndex * GOLDEN_ANGLE_DEGREES) % FULL_HUE_DEGREES
    val saturation = if ((generatedIndex / HUE_VARIANTS) % 2 == 0) 0.58f else 0.68f
    val value = if ((generatedIndex / (HUE_VARIANTS * 2)) % 2 == 0) 0.94f else 0.86f

    return Color.hsv(hue, saturation, value)
}

private val chartPalette = listOf(
    Color(0xFFB69DF8),
    Color(0xFFF48FB1),
    Color(0xFF4FD8EB),
    Color(0xFFFFB26B),
    Color(0xFF7DD9A5),
    Color(0xFFFFD166),
    Color(0xFF8EA7F8),
    Color(0xFFD98CE5),
    Color(0xFF65C6B4),
    Color(0xFFFF8F80),
    Color(0xFFA7D96F),
    Color(0xFF72B7E8)
)

private const val GOLDEN_ANGLE_DEGREES = 137.50776f
private const val FULL_HUE_DEGREES = 360f
private const val HUE_VARIANTS = 12
