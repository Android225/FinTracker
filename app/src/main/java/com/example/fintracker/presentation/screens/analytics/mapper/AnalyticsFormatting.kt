package com.example.fintracker.presentation.screens.analytics.mapper

import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

internal fun AnalyticsPeriod.formatForDisplay(): String =
    "${start.format(dateFormatter)} – ${end.format(dateFormatter)}"

internal val AnalyticsTransactionType.displayName: String
    get() = when (this) {
        AnalyticsTransactionType.Expense -> "Расходы"
        AnalyticsTransactionType.Income -> "Доходы"
        AnalyticsTransactionType.All -> "Всё"
    }
