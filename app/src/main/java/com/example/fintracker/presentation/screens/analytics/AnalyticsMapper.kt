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
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

internal fun AnalyticsCalculation.toUiContent(
    filter: AnalyticsFilter,
    categories: List<Category>,
    accounts: List<AccountBrief>,
    today: LocalDate,
    selectedCurrency: String,
    currencyOptions: List<String>,
    convert: (BigDecimal, String, String) -> BigDecimal
): AnalyticsContent {
    val availableCategories = categories.filter { category ->
        when (filter.type) {
            TransactionType.Expense -> !category.isIncome
            TransactionType.Income -> category.isIncome
            TransactionType.All -> true
        }
    }
    val showCurrencySelector = filter.accountId == null
    val displayCurrency = selectedCurrency
    val convertedAmounts = transactions.associateWith { transaction ->
        convert(transaction.amount, transaction.account.currency, displayCurrency)
    }
    val totalAmount = convertedAmounts.values.fold(BigDecimal.ZERO, BigDecimal::add)
    val convertedBreakdown = transactions
        .groupBy(Transaction::category)
        .map { (category, categoryTransactions) ->
            val amount = categoryTransactions.fold(BigDecimal.ZERO) { sum, transaction ->
                sum + requireNotNull(convertedAmounts[transaction])
            }
            amount to AnalyticsBreakdownUi(
                categoryId = category.id,
                title = category.name,
                amount = amount.formatMoney(displayCurrency),
                percent = amount.percentOf(totalAmount)
            )
        }
        .sortedByDescending { (amount) -> amount }
        .map { (_, item) -> item }

    return AnalyticsContent(
        totalAmount = totalAmount.formatMoney(displayCurrency),
        selectedCurrency = displayCurrency,
        currencyOptions = currencyOptions,
        showCurrencySelector = showCurrencySelector,
        filter = filter.toUi(availableCategories, accounts),
        breakdown = convertedBreakdown,
        transactions = transactions.map { transaction ->
            transaction.toUi(
                displayCurrency = displayCurrency,
                convertedAmount = requireNotNull(convertedAmounts[transaction])
            )
        },
        typeOptions = TransactionType.entries,
        periodOptions = createPeriodOptions(filter, today),
        categoryOptions = createCategoryOptions(availableCategories, filter.categoryIds),
        accountOptions = createAccountOptions(accounts, filter.accountId),
        selectedType = filter.type,
        selectedPeriod = filter.period
    )
}
private fun AnalyticsFilter.toUi(
    categories: List<Category>,
    accounts: List<AccountBrief>
): AnalyticsFilterUi {
    val selectedCategories = categories.filter { it.id in categoryIds }
    return AnalyticsFilterUi(
        type = type,
        period = period.formatForDisplay(),
        categories = if (selectedCategories.isEmpty()) {
            ""
        } else {
            selectedCategories.joinToString { it.name }
        },
        account = accounts.firstOrNull { it.id == accountId }?.name
    )
}

private fun Transaction.toUi(
    displayCurrency: String,
    convertedAmount: BigDecimal
): AnalyticsTransactionUi =
    AnalyticsTransactionUi(
        id = id,
        emoji = category.emoji,
        title = comment ?: category.name,
        subtitle = account.name,
        amount = convertedAmount.formatMoney(displayCurrency)
    )

private fun BigDecimal.percentOf(total: BigDecimal): Int {
    if (total <= BigDecimal.ZERO) return 0
    return multiply(BigDecimal(100))
        .divide(total, 0, RoundingMode.HALF_UP)
        .toInt()
}

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
            subtitle = filter.period.formatForDisplay(),
            selected = filter.periodKind == AnalyticsPeriodKind.Custom
        ),
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Week,
            period = week,
            selected = filter.periodKind == AnalyticsPeriodKind.Week
        ),
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Month,
            period = month,
            selected = filter.periodKind == AnalyticsPeriodKind.Month
        ),
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Quarter,
            period = quarter,
            selected = filter.periodKind == AnalyticsPeriodKind.Quarter
        ),
        AnalyticsPeriodOption(
            kind = AnalyticsPeriodKind.Year,
            period = year,
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
    selectedAccountId: Long?
): List<AnalyticsAccountOption> {
    val allAccounts = AnalyticsAccountOption(
        id = null,
        title = "",
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
