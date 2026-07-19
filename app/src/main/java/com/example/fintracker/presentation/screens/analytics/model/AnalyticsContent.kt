package com.example.fintracker.presentation.screens.analytics.model

import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType

// Полное содержимое экрана аналитики.
data class AnalyticsContent(
    val totalAmount: String,
    val filter: AnalyticsFilterUi,
    val breakdown: List<AnalyticsBreakdownUi>,
    val transactions: List<AnalyticsTransactionUi>,
    val typeOptions: List<AnalyticsTransactionType>,
    val periodOptions: List<AnalyticsPeriodOption>,
    val categoryOptions: List<AnalyticsCategoryOption>,
    val accountOptions: List<AnalyticsAccountOption>,
    val selectedType: AnalyticsTransactionType,
    val selectedPeriod: AnalyticsPeriod
)

// Отображаемые значения выбранных фильтров.
data class AnalyticsFilterUi(
    val type: String,
    val period: String,
    val categories: String,
    val account: String
)
