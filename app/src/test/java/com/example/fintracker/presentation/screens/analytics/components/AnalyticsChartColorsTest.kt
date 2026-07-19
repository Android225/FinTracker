package com.example.fintracker.presentation.screens.analytics.components

import androidx.compose.ui.graphics.toArgb
import com.example.fintracker.presentation.screens.analytics.components.chart.calculateSweepAngles
import com.example.fintracker.presentation.screens.analytics.components.chart.toAnalyticsChartSegments
import com.example.fintracker.presentation.screens.analytics.components.chart.toOverviewChartSegments
import com.example.fintracker.presentation.screens.analytics.AnalyticsBreakdownUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    @Test
    fun `overview keeps every category when there are five`() {
        val breakdown = (1..5).map { categoryId ->
            AnalyticsBreakdownUi(
                categoryId = categoryId,
                title = "Category $categoryId",
                amount = "100 ₽",
                percent = 20
            )
        }

        val segments = breakdown.toOverviewChartSegments()

        assertEquals(listOf(1, 2, 3, 4, 5), segments.map { it.breakdown.categoryId })
    }

    @Test
    fun `overview keeps top four categories and groups the rest`() {
        val breakdown = listOf(
            AnalyticsBreakdownUi(1, "First", "30 ₽", 30),
            AnalyticsBreakdownUi(2, "Second", "25 ₽", 25),
            AnalyticsBreakdownUi(3, "Third", "20 ₽", 20),
            AnalyticsBreakdownUi(4, "Fourth", "15 ₽", 15),
            AnalyticsBreakdownUi(5, "Fifth", "6 ₽", 6),
            AnalyticsBreakdownUi(6, "Sixth", "4 ₽", 4)
        )

        val segments = breakdown.toOverviewChartSegments()

        assertEquals(listOf(1, 2, 3, 4), segments.take(4).map { it.breakdown.categoryId })
        assertEquals("Другие", segments.last().breakdown.title)
        assertEquals(10, segments.last().breakdown.percent)
        assertEquals(5, segments.size)
        assertEquals(segments.size, segments.map { it.color }.toSet().size)
    }

    @Test
    fun `zero rounded category keeps a visible one percent sector`() {
        val segments = listOf(
            AnalyticsBreakdownUi(1, "Main", "990 ₽", 99),
            AnalyticsBreakdownUi(2, "Small", "10 ₽", 1),
            AnalyticsBreakdownUi(3, "Tiny", "1 ₽", 0)
        ).toAnalyticsChartSegments()

        val sweepAngles = segments.calculateSweepAngles()

        assertEquals(360f, sweepAngles.sum(), 0.001f)
        assertEquals(3.6f, sweepAngles[1], 0.001f)
        assertEquals(3.6f, sweepAngles[2], 0.001f)
        assertTrue(sweepAngles.all { it > 0f })
    }
}
