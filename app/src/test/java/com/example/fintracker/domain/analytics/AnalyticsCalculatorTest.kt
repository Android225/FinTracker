package com.example.fintracker.domain.analytics

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class AnalyticsCalculatorTest {
    @Test
    fun `calculates total and breakdown for selected transaction type`() {
        val result = AnalyticsCalculator.calculate(
            transactions = listOf(
                transaction(1, "100", "2026-07-10T10:00:00Z", 1, 1),
                transaction(2, "50", "2026-07-11T10:00:00Z", 1, 2),
                transaction(3, "500", "2026-07-12T10:00:00Z", 1, 3, isIncome = true)
            ),
            filter = filter(type = TransactionType.Expense),
            zoneId = ZoneOffset.UTC
        )

        assertEquals(BigDecimal("150"), result.totalAmount)
        assertEquals(listOf(2, 1), result.transactions.map { it.id })
        assertEquals(listOf(1, 2), result.breakdown.map { it.categoryId })
        assertEquals(listOf(67, 33), result.breakdown.map { it.percent })
    }

    @Test
    fun `applies period category and account filters together`() {
        val result = AnalyticsCalculator.calculate(
            transactions = listOf(
                transaction(1, "100", "2026-07-10T10:00:00Z", 1, 1),
                transaction(2, "200", "2026-07-10T10:00:00Z", 2, 1),
                transaction(3, "300", "2026-06-30T10:00:00Z", 1, 1),
                transaction(4, "400", "2026-07-10T10:00:00Z", 1, 2)
            ),
            filter = filter(
                type = TransactionType.All,
                categoryIds = setOf(1),
                accountId = 1
            ),
            zoneId = ZoneOffset.UTC
        )

        assertEquals(listOf(1), result.transactions.map { it.id })
        assertEquals(BigDecimal("100"), result.totalAmount)
    }

    @Test
    fun `period rejects an end date before its start date`() {
        assertThrows(IllegalArgumentException::class.java) {
            AnalyticsPeriod(
                start = LocalDate.of(2026, 7, 2),
                end = LocalDate.of(2026, 7, 1)
            )
        }
    }

    private fun filter(
        type: TransactionType,
        categoryIds: Set<Int> = emptySet(),
        accountId: Int? = null
    ): AnalyticsFilter = AnalyticsFilter(
        type = type,
        period = AnalyticsPeriod(
            start = LocalDate.of(2026, 7, 1),
            end = LocalDate.of(2026, 7, 31)
        ),
        periodKind = AnalyticsPeriodKind.Month,
        categoryIds = categoryIds,
        accountId = accountId
    )

    private fun transaction(
        id: Int,
        amount: String,
        date: String,
        accountId: Int,
        categoryId: Int,
        isIncome: Boolean = false
    ): Transaction {
        val timestamp = Instant.parse(date)
        return Transaction(
            id = id,
            account = AccountBrief(
                id = accountId,
                name = "Account $accountId",
                emoji = "💳",
                balance = BigDecimal.ZERO,
                currency = "RUB"
            ),
            category = Category(categoryId, "Category $categoryId", "", isIncome),
            amount = BigDecimal(amount),
            transactionDate = timestamp,
            comment = null,
            createdAt = timestamp,
            updatedAt = timestamp
        )
    }
}
