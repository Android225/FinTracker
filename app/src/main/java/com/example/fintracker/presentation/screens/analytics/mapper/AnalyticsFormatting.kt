package com.example.fintracker.presentation.screens.analytics.mapper

import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

private val moneyFormat = DecimalFormat("#,###")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

internal fun BigDecimal.formatMoney(): String =
    "${moneyFormat.format(this).replace(',', ' ')} ₽"

internal fun AnalyticsPeriod.formatForDisplay(): String =
    "${start.format(dateFormatter)} – ${end.format(dateFormatter)}"

internal val AnalyticsTransactionType.displayName: String
    get() = when (this) {
        AnalyticsTransactionType.Expense -> "Расходы"
        AnalyticsTransactionType.Income -> "Доходы"
        AnalyticsTransactionType.All -> "Всё"
    }
