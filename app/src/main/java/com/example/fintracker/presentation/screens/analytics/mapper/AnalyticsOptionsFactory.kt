package com.example.fintracker.presentation.screens.analytics.mapper

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import com.example.fintracker.presentation.screens.analytics.AnalyticsAccountOption
import com.example.fintracker.presentation.screens.analytics.AnalyticsCategoryOption
import com.example.fintracker.presentation.screens.analytics.AnalyticsPeriodOption
import java.time.LocalDate

internal fun createPeriodOptions(
    filter: AnalyticsFilter,
    today: LocalDate
): List<AnalyticsPeriodOption> {
    val week = AnalyticsPeriod(today.minusDays(6), today)
    val month = AnalyticsPeriod(today.withDayOfMonth(1), today)
    val quarterStartMonth = ((today.monthValue - 1) / 3) * 3 + 1
    val quarter = AnalyticsPeriod(LocalDate.of(today.year, quarterStartMonth, 1), today)
    val year = AnalyticsPeriod(today.withDayOfYear(1), today)

    return listOf(
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Custom,
            period = filter.period,
            title = "Произвольный",
            subtitle = filter.period.formatForDisplay(),
            selected = filter.periodKind == AnalyticsPeriodKind.Custom
        ),
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Week,
            period = week,
            title = "За неделю",
            selected = filter.periodKind == AnalyticsPeriodKind.Week
        ),
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Month,
            period = month,
            title = "За месяц",
            selected = filter.periodKind == AnalyticsPeriodKind.Month
        ),
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Quarter,
            period = quarter,
            title = "За квартал",
            selected = filter.periodKind == AnalyticsPeriodKind.Quarter
        ),
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Year,
            period = year,
            title = "За год",
            selected = filter.periodKind == AnalyticsPeriodKind.Year
        )
    )
}

internal fun createCategoryOptions(
    categories: List<Category>,
    selectedIds: Set<Int>
): List<AnalyticsCategoryOption> = categories.map { category ->
    AnalyticsCategoryOption(
        id = category.id,
        title = category.name,
        emoji = category.emoji,
        selected = category.id in selectedIds
    )
}

internal fun createAccountOptions(
    accounts: List<AccountBrief>,
    selectedAccountId: Int?
): List<AnalyticsAccountOption> {
    val allAccounts = AnalyticsAccountOption(
        id = null,
        title = "Все счета",
        emoji = "💳",
        selected = selectedAccountId == null
    )

    return listOf(allAccounts) + accounts.map { account ->
        AnalyticsAccountOption(
            id = account.id,
            title = account.name,
            emoji = account.emoji,
            selected = account.id == selectedAccountId
        )
    }
}
