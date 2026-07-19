package com.example.fintracker.domain.analytics

import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.analytics.model.AnalyticsCalculation
import com.example.fintracker.domain.analytics.model.AnalyticsCategoryBreakdown
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZoneId

object AnalyticsCalculator {
    // Применяет фильтры и строит сумму с разбивкой по категориям вне UI-слоя.
    fun calculate(
        transactions: List<Transaction>,
        filter: AnalyticsFilter,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): AnalyticsCalculation {
        val filteredTransactions = transactions
            .asSequence()
            .filter { filter.type.isIncome == null || it.category.isIncome == filter.type.isIncome }
            .filter {
                it.transactionDate.atZone(zoneId).toLocalDate() in
                    filter.period.start..filter.period.end
            }
            .filter { filter.categoryIds.isEmpty() || it.category.id in filter.categoryIds }
            .filter { filter.accountId == null || it.account.id == filter.accountId }
            .sortedByDescending(Transaction::transactionDate)
            .toList()

        val totalAmount = filteredTransactions.sumOfAmount()
        val breakdown = filteredTransactions
            .groupBy(Transaction::category)
            .map { (category, categoryTransactions) ->
                val amount = categoryTransactions.sumOfAmount()
                AnalyticsCategoryBreakdown(
                    categoryId = category.id,
                    title = category.name,
                    amount = amount,
                    percent = amount.percentOf(totalAmount)
                )
            }
            .sortedByDescending(AnalyticsCategoryBreakdown::amount)

        return AnalyticsCalculation(
            totalAmount = totalAmount,
            transactions = filteredTransactions,
            breakdown = breakdown
        )
    }
}

private fun List<Transaction>.sumOfAmount(): BigDecimal =
    fold(BigDecimal.ZERO) { sum, transaction -> sum + transaction.amount }

// Рассчитывает целый процент категории и защищается от деления на ноль.
private fun BigDecimal.percentOf(total: BigDecimal): Int {
    if (total <= BigDecimal.ZERO) return 0
    return multiply(BigDecimal(100))
        .divide(total, 0, RoundingMode.HALF_UP)
        .toInt()
}
