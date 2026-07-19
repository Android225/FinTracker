package com.example.fintracker.presentation.screens.analytics.model

import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType

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

data class AnalyticsFilterUi(
    val type: String,
    val period: String,
    val categories: String,
    val account: String
)
