package com.example.fintracker.presentation.screens.analytics.model

import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType

sealed interface AnalyticsEvent {
    data object ScreenVisible : AnalyticsEvent
    data object Refresh : AnalyticsEvent
    data class SelectType(val type: AnalyticsTransactionType) : AnalyticsEvent
    data class SelectPeriod(
        val kind: AnalyticsPeriodKind,
        val period: AnalyticsPeriod
    ) : AnalyticsEvent

    data class SelectCategories(val categoryIds: Set<Int>) : AnalyticsEvent
    data class SelectAccount(val accountId: Int?) : AnalyticsEvent
}
