package com.example.fintracker.domain.analytics.model

data class AnalyticsFilter(
    val type: AnalyticsTransactionType,
    val period: AnalyticsPeriod,
    val periodKind: AnalyticsPeriodKind,
    val categoryIds: Set<Int>,
    val accountId: Int?
)
