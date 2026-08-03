package com.example.fintracker.domain.analytics.model

import com.example.fintracker.domain.TransactionType

data class AnalyticsFilter(
    val type: TransactionType,
    val period: AnalyticsPeriod,
    val periodKind: AnalyticsPeriodKind,
    val categoryIds: Set<Int>,
    val accountId: Long?
)
