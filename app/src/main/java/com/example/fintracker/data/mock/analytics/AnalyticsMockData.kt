package com.example.fintracker.data.mock.analytics

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import java.math.BigDecimal
import java.time.Instant

internal val analyticsMockCategories = listOf(
    Category(1, "Ремонт", "🔧", isIncome = false),
    Category(2, "Авто", "🚗", isIncome = false),
    Category(3, "Другое", "📦", isIncome = false),
    Category(4, "Продукты", "🛒", isIncome = false),
    Category(5, "Транспорт", "🚌", isIncome = false),
    Category(9, "Премия", "💼", isIncome = true),
    Category(10, "Аренда", "🏠", isIncome = true)
)

internal val analyticsMockAccounts = listOf(
    AccountBrief(2, "Сбербанк", "🏦", BigDecimal("122322"), "RUB"),
    AccountBrief(1, "Тинькофф", "💳", BigDecimal("122322"), "RUB"),
    AccountBrief(3, "Наличные", "💵", BigDecimal("123322"), "RUB")
)

private fun category(id: Int): Category = analyticsMockCategories.first { it.id == id }
private fun account(id: Int): AccountBrief = analyticsMockAccounts.first { it.id == id }

internal val analyticsMockTransactions = listOf(
    transaction(1, "80200", "2026-07-16T12:00:00Z", "Ремонт квартиры", 1, 1),
    transaction(2, "31500", "2026-07-15T09:30:00Z", "ТО и бензин", 2, 2),
    transaction(3, "20544", "2026-07-13T18:20:00Z", "На собачку", 3, 3),
    transaction(4, "45100", "2026-07-09T15:45:00Z", "Покупки в магазине", 2, 4),
    transaction(5, "25000", "2026-07-11T10:00:00Z", "Премия за проект", 1, 9),
    transaction(6, "38000", "2026-07-05T10:00:00Z", "Сдача квартиры", 2, 10)
)

private fun transaction(
    id: Int,
    amount: String,
    transactionDate: String,
    comment: String,
    accountId: Int,
    categoryId: Int
): Transaction {
    val timestamp = Instant.parse(transactionDate)
    return Transaction(
        id = id,
        amount = BigDecimal(amount),
        transactionDate = timestamp,
        comment = comment,
        createdAt = timestamp,
        updatedAt = timestamp,
        account = account(accountId),
        category = category(categoryId)
    )
}
