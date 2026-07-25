package com.example.fintracker.presentation.screens.analytics

import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind

sealed interface AnalyticsEvent {
    data object ScreenVisible : AnalyticsEvent
    data object Refresh : AnalyticsEvent
    data class SelectType(val type: TransactionType) : AnalyticsEvent
    data class SelectPeriod(
        val kind: AnalyticsPeriodKind,
        val period: AnalyticsPeriod
    ) : AnalyticsEvent

    data class SelectCategories(val categoryIds: Set<Int>) : AnalyticsEvent
    data class SelectAccount(val accountId: Long?) : AnalyticsEvent
    data class SelectCurrency(val currency: String) : AnalyticsEvent
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
    val selectedCurrency: String,
    val currencyOptions: List<String>,
    val showCurrencySelector: Boolean,
    val filter: AnalyticsFilterUi,
    val breakdown: List<AnalyticsBreakdownUi>,
    val transactions: List<AnalyticsTransactionUi>,
    val typeOptions: List<TransactionType>,
    val periodOptions: List<AnalyticsPeriodOption>,
    val categoryOptions: List<AnalyticsCategoryOption>,
    val accountOptions: List<AnalyticsAccountOption>,
    val selectedType: TransactionType,
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
    val id: Long,
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
    val id: Long?,
    val title: String,
    val subtitle: String? = null,
    val emoji: String,
    val selected: Boolean
)
