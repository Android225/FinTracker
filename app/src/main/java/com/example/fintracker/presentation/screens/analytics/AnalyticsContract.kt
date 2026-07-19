package com.example.fintracker.presentation.screens.analytics

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

enum class AnalyticsSheet {
    Detail,
    Type,
    Period,
    Categories,
    Account,
    CustomPeriod
}

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

data class AnalyticsBreakdownUi(
    val categoryId: Int,
    val title: String,
    val amount: String,
    val percent: Int
)

data class AnalyticsTransactionUi(
    val id: Int,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val amount: String
)

data class AnalyticsPeriodOption(
    val kind: AnalyticsPeriodKind,
    val period: AnalyticsPeriod,
    val title: String,
    val subtitle: String? = null,
    val selected: Boolean
)

data class AnalyticsCategoryOption(
    val id: Int,
    val title: String,
    val emoji: String,
    val selected: Boolean
)

data class AnalyticsAccountOption(
    val id: Int?,
    val title: String,
    val subtitle: String? = null,
    val emoji: String,
    val selected: Boolean
)
