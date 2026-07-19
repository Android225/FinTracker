package com.example.fintracker.presentation.screens.analytics.components

import androidx.compose.ui.graphics.toArgb
import com.example.fintracker.presentation.screens.analytics.components.chart.toAnalyticsChartSegments
import com.example.fintracker.presentation.screens.analytics.AnalyticsBreakdownUi
import org.junit.Assert.assertEquals
import org.junit.Test

class AnalyticsChartColorsTest {
    @Test
    fun `assigns a distinct color to every displayed category`() {
        val breakdown = (1..100).map { categoryId ->
            AnalyticsBreakdownUi(
                categoryId = categoryId,
                title = "Category $categoryId",
                amount = "100 ₽",
                percent = 1
            )
        }

        val colors = breakdown.toAnalyticsChartSegments().map { it.color }

        assertEquals(breakdown.size, colors.toSet().size)
    }

    @Test
    fun `keeps category colors stable when breakdown order changes`() {
        val source = listOf(
            AnalyticsBreakdownUi(3, "Other", "100 ₽", 40),
            AnalyticsBreakdownUi(1, "Food", "80 ₽", 32),
            AnalyticsBreakdownUi(2, "Transport", "70 ₽", 28)
        )

        val originalColors = source.toAnalyticsChartSegments()
            .associate { it.breakdown.categoryId to it.color }
        val reorderedColors = source.reversed().toAnalyticsChartSegments()
            .associate { it.breakdown.categoryId to it.color }

        assertEquals(originalColors, reorderedColors)
    }

    @Test
    fun `starts with colors used by the Figma chart`() {
        val segments = listOf(
            AnalyticsBreakdownUi(1, "Food", "40 ₽", 40),
            AnalyticsBreakdownUi(2, "Transport", "35 ₽", 35),
            AnalyticsBreakdownUi(3, "Other", "25 ₽", 25)
        )
            .toAnalyticsChartSegments()
            .map { it.color.toArgb() }

        assertEquals(
            listOf(
                0xFFB69DF8.toInt(),
                0xFFF48FB1.toInt(),
                0xFF4FD8EB.toInt()
            ),
            segments
        )
    }
}
