package com.example.fintracker.presentation.screens.analytics

import java.time.LocalDate

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

data class AnalyticsPeriod(
    val start: LocalDate,
    val end: LocalDate
) {
    init {
        require(!end.isBefore(start)) { "Analytics period end must not precede start" }
    }
}

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

internal data class AnalyticsFilter(
    val type: AnalyticsTransactionType,
    val period: AnalyticsPeriod,
    val periodKind: AnalyticsPeriodKind,
    val categoryIds: Set<Int>,
    val accountId: Int?
)

enum class AnalyticsTransactionType(
    val label: String,
    val isIncome: Boolean?
) {
    Expense("Расходы", false),
    Income("Доходы", true),
    All("Всё", null)
}

enum class AnalyticsPeriodKind {
    Custom,
    Week,
    Month,
    Quarter,
    Year
}

sealed interface AnalyticsEvent {
    data object Refresh : AnalyticsEvent
    data object ResetFilters : AnalyticsEvent
    data class SelectType(val type: AnalyticsTransactionType) : AnalyticsEvent
    data class SelectPeriod(
        val kind: AnalyticsPeriodKind,
        val period: AnalyticsPeriod
    ) : AnalyticsEvent
    data class SelectCategories(val categoryIds: Set<Int>) : AnalyticsEvent
    data class SelectAccount(val accountId: Int?) : AnalyticsEvent
}
