package com.example.fintracker.domain.analytics.model

import java.time.LocalDate

// Выбранный диапазон дат для аналитики.
data class AnalyticsPeriod(
    val start: LocalDate,
    val end: LocalDate
) {
    init {
        require(!end.isBefore(start)) { "Analytics period end must not precede start" }
    }
}
