package com.example.fintracker.domain.analytics

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.TransactionResponse
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

// Проверяет фильтрацию и расчёты доменной аналитики.
class AnalyticsCalculatorTest {
    // Проверяет сумму, порядок и проценты выбранного типа операций.
    @Test
    fun `calculates total and breakdown for selected transaction type`() {
        val result = AnalyticsCalculator.calculate(
            transactions = listOf(
                transaction(1, "100", "2026-07-10T10:00:00", accountId = 1, categoryId = 1),
                transaction(2, "50", "2026-07-11T10:00:00", accountId = 1, categoryId = 2),
                transaction(
                    id = 3,
                    amount = "500",
                    date = "2026-07-12T10:00:00",
                    accountId = 1,
                    categoryId = 3,
                    isIncome = true
                )
            ),
            filter = filter(type = AnalyticsTransactionType.Expense)
        )

        assertEquals(BigDecimal("150"), result.totalAmount)
        assertEquals(listOf(2, 1), result.transactions.map { it.id })
        assertEquals(listOf(1, 2), result.breakdown.map { it.categoryId })
        assertEquals(listOf(67, 33), result.breakdown.map { it.percent })
    }

    // Проверяет совместную работу всех фильтров.
    @Test
    fun `applies period category and account filters together`() {
        val result = AnalyticsCalculator.calculate(
            transactions = listOf(
                transaction(1, "100", "2026-07-10T10:00:00", accountId = 1, categoryId = 1),
                transaction(2, "200", "2026-07-10T10:00:00", accountId = 2, categoryId = 1),
                transaction(3, "300", "2026-06-30T10:00:00", accountId = 1, categoryId = 1),
                transaction(4, "400", "2026-07-10T10:00:00", accountId = 1, categoryId = 2)
            ),
            filter = filter(
                type = AnalyticsTransactionType.All,
                categoryIds = setOf(1),
                accountId = 1
            )
        )

        assertEquals(listOf(1), result.transactions.map { it.id })
        assertEquals(BigDecimal("100"), result.totalAmount)
    }

    // Проверяет защиту от некорректного диапазона дат.
    @Test
    fun `period rejects an end date before its start date`() {
        assertThrows(IllegalArgumentException::class.java) {
            AnalyticsPeriod(
                start = LocalDate.of(2026, 7, 2),
                end = LocalDate.of(2026, 7, 1)
            )
        }
    }

    // Создаёт фильтр для тестового сценария.
    private fun filter(
        type: AnalyticsTransactionType,
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

    // Создаёт операцию для тестового сценария.
    private fun transaction(
        id: Int,
        amount: String,
        date: String,
        accountId: Int,
        categoryId: Int,
        isIncome: Boolean = false
    ): TransactionResponse = TransactionResponse(
        id = id,
        account = AccountBrief(accountId, "Account $accountId", "0", "RUB"),
        category = Category(categoryId, "Category $categoryId", "", isIncome),
        amount = amount,
        transactionDate = date,
        comment = null,
        createdAt = "",
        updatedAt = ""
    )
}
