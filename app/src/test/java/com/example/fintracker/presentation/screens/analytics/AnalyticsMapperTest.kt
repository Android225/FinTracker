package com.example.fintracker.presentation.screens.analytics

import com.example.fintracker.data.repository.FixedExchangeRatesRepository
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.analytics.model.AnalyticsCalculation
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class AnalyticsMapperTest {
    private val rates = FixedExchangeRatesRepository()
    private val period = AnalyticsPeriod(
        start = LocalDate.of(2026, 7, 1),
        end = LocalDate.of(2026, 7, 31)
    )
    private val rubAccount = account(id = 1, currency = "RUB")
    private val usdAccount = account(id = 2, currency = "USD")

    @Test
    fun `all accounts are converted to selected currency`() {
        val content = calculation(
            transaction(id = 1, amount = "900", account = rubAccount),
            transaction(id = 2, amount = "10", account = usdAccount)
        ).toUiContent(
            filter = filter(accountId = null),
            categories = listOf(category),
            accounts = listOf(rubAccount, usdAccount),
            today = period.end,
            selectedCurrency = "USD",
            currencyOptions = rates.supportedCurrencies,
            convert = rates::convert
        )

        assertTrue(content.showCurrencySelector)
        assertEquals("USD", content.selectedCurrency)
        assertEquals("20 $", content.totalAmount)
        assertEquals(listOf("10 $", "10 $"), content.transactions.map { it.amount })
        assertEquals(TransactionType.Expense, content.filter.type)
        assertEquals("", content.filter.categories)
        assertEquals(null, content.filter.account)
        assertEquals("", content.accountOptions.first().title)
    }

    @Test
    fun `single account still uses global currency and hides selector`() {
        val content = calculation(
            transaction(id = 2, amount = "10", account = usdAccount)
        ).toUiContent(
            filter = filter(accountId = usdAccount.id),
            categories = listOf(category),
            accounts = listOf(rubAccount, usdAccount),
            today = period.end,
            selectedCurrency = "RUB",
            currencyOptions = rates.supportedCurrencies,
            convert = rates::convert
        )

        assertFalse(content.showCurrencySelector)
        assertEquals("RUB", content.selectedCurrency)
        assertEquals("900 ₽", content.totalAmount)
    }

    private fun calculation(vararg transactions: Transaction) = AnalyticsCalculation(
        totalAmount = BigDecimal.ZERO,
        transactions = transactions.toList(),
        breakdown = emptyList()
    )

    private fun filter(accountId: Long?) = AnalyticsFilter(
        type = TransactionType.Expense,
        period = period,
        periodKind = AnalyticsPeriodKind.Month,
        categoryIds = emptySet(),
        accountId = accountId
    )

    private fun account(id: Long, currency: String) = AccountBrief(
        id = id,
        serverId = id.toInt(),
        name = currency,
        emoji = "",
        balance = BigDecimal.ZERO,
        currency = currency
    )

    private fun transaction(
        id: Long,
        amount: String,
        account: AccountBrief
    ) = Transaction(
        id = id,
        serverId = id.toInt(),
        account = account,
        category = category,
        amount = BigDecimal(amount),
        transactionDate = Instant.parse("2026-07-10T12:00:00Z"),
        comment = null,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )

    private companion object {
        val category = Category(
            id = 1,
            name = "Test",
            emoji = "",
            isIncome = false
        )
    }
}
