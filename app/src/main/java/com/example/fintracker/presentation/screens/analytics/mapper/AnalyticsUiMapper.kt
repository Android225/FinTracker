package com.example.fintracker.presentation.screens.analytics.mapper

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.analytics.model.AnalyticsCalculation
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType
import com.example.fintracker.presentation.common.format.formatMoney
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsBreakdownUi
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsContent
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsFilterUi
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsTransactionUi
import java.time.LocalDate

internal fun AnalyticsCalculation.toUiContent(
    filter: AnalyticsFilter,
    categories: List<Category>,
    accounts: List<AccountBrief>,
    today: LocalDate
): AnalyticsContent {
    val availableCategories = categories.filter { category ->
        filter.type.isIncome == null || category.isIncome == filter.type.isIncome
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
        typeOptions = AnalyticsTransactionType.entries,
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
