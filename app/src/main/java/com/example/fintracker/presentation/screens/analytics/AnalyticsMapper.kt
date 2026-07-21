package com.example.fintracker.presentation.screens.analytics

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.analytics.model.AnalyticsCalculation
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import com.example.fintracker.presentation.common.format.formatMoney
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

internal fun AnalyticsCalculation.toUiContent(
    filter: AnalyticsFilter,
    categories: List<Category>,
    accounts: List<AccountBrief>,
    today: LocalDate
): AnalyticsContent {
    val availableCategories = categories.filter { category ->
        when (filter.type) {
            TransactionType.Expense -> !category.isIncome
            TransactionType.Income -> category.isIncome
            TransactionType.All -> true
        }
    }

    return AnalyticsContent(
        totalAmount = totalAmount.formatMoney(),
        filter = filter.toUi(availableCategories, accounts),
        breakdown = breakdown.map { item ->
            AnalyticsBreakdownUi(
                categoryId = item.categoryId,
                title = item.title,
                amount = item.amount.formatMoney(),
                percent = item.percent
            )
        },
        transactions = transactions.map(Transaction::toUi),
        typeOptions = TransactionType.entries,
        periodOptions = createPeriodOptions(filter, today),
        categoryOptions = createCategoryOptions(availableCategories, filter.categoryIds),
        accountOptions = createAccountOptions(accounts, filter.accountId),
        selectedType = filter.type,
        selectedPeriod = filter.period
    )
}
//TODO("В 3 передавать контекст дял stringResouces() for localization")
private fun AnalyticsFilter.toUi(
    categories: List<Category>,
    accounts: List<AccountBrief>
): AnalyticsFilterUi {
    val selectedCategories = categories.filter { it.id in categoryIds }
    return AnalyticsFilterUi(
        type = type.displayName,
        period = period.formatForDisplay(),
        categories = if (selectedCategories.isEmpty()) {
            "Все статьи"
        } else {
            selectedCategories.joinToString { it.name }
        },
        account = accounts.firstOrNull { it.id == accountId }?.name ?: "Все счета"
    )
}

private fun Transaction.toUi(): AnalyticsTransactionUi =
    AnalyticsTransactionUi(
        id = id,
        emoji = category.emoji,
        title = comment ?: category.name,
        subtitle = account.name,
        amount = amount.formatMoney()
    )

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

internal fun AnalyticsPeriod.formatForDisplay(): String =
    "${start.format(dateFormatter)} – ${end.format(dateFormatter)}"

internal val TransactionType.displayName: String
    get() = when (this) {
        TransactionType.Expense -> "Расходы"
        TransactionType.Income -> "Доходы"
        TransactionType.All -> "Всё"
    }
