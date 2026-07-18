package com.example.fintracker.domain.analytics

import com.example.fintracker.domain.TransactionResponse
import com.example.fintracker.domain.analytics.model.AnalyticsCalculation
import com.example.fintracker.domain.analytics.model.AnalyticsCategoryBreakdown
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object AnalyticsCalculator {
    fun calculate(
        transactions: List<TransactionResponse>,
        filter: AnalyticsFilter
    ): AnalyticsCalculation {
        val filteredTransactions = transactions
            .asSequence()
            .filter { filter.type.isIncome == null || it.category.isIncome == filter.type.isIncome }
            .filter { it.transactionDate.toLocalDate() in filter.period.start..filter.period.end }
            .filter { filter.categoryIds.isEmpty() || it.category.id in filter.categoryIds }
            .filter { filter.accountId == null || it.account.id == filter.accountId }
            .sortedByDescending { it.transactionDate.toLocalDateTime() }
            .toList()

        val totalAmount = filteredTransactions.sumOfAmount()
        val breakdown = filteredTransactions
            .groupBy(TransactionResponse::category)
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

private fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

private fun String.toLocalDate(): LocalDate = toLocalDateTime().toLocalDate()

private fun List<TransactionResponse>.sumOfAmount(): BigDecimal =
    fold(BigDecimal.ZERO) { sum, transaction -> sum + transaction.amount.toBigDecimal() }

private fun BigDecimal.percentOf(total: BigDecimal): Int {
    if (total <= BigDecimal.ZERO) return 0
    return multiply(BigDecimal(100))
        .divide(total, 0, RoundingMode.HALF_UP)
        .toInt()
}
