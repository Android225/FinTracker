package com.example.fintracker.data.mock.analytics

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.TransactionResponse

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
    AccountBrief(2, "Сбербанк", "122322", "RUB"),
    AccountBrief(1, "Тинькофф", "122322", "RUB"),
    AccountBrief(3, "Наличные", "123322", "RUB")
)

private fun category(id: Int): Category = analyticsMockCategories.first { it.id == id }
private fun account(id: Int): AccountBrief = analyticsMockAccounts.first { it.id == id }

internal val analyticsMockTransactions = listOf(
    TransactionResponse(
        id = 1,
        amount = "80200",
        transactionDate = "2026-07-16T12:00:00",
        comment = "Ремонт квартиры",
        createdAt = "",
        updatedAt = "",
        account = account(1),
        category = category(1)
    ),
    TransactionResponse(
        id = 2,
        amount = "31500",
        transactionDate = "2026-07-15T09:30:00",
        comment = "ТО и бензин",
        createdAt = "",
        updatedAt = "",
        account = account(2),
        category = category(2)
    ),
    TransactionResponse(
        id = 3,
        amount = "20544",
        transactionDate = "2026-07-13T18:20:00",
        comment = "На собачку",
        createdAt = "",
        updatedAt = "",
        account = account(3),
        category = category(3)
    ),
    TransactionResponse(
        id = 4,
        amount = "45100",
        transactionDate = "2026-07-09T15:45:00",
        comment = "Покупки в магазине",
        createdAt = "",
        updatedAt = "",
        account = account(2),
        category = category(4)
    ),
    TransactionResponse(
        id = 5,
        amount = "25000",
        transactionDate = "2026-07-11T10:00:00",
        comment = "Премия за проект",
        createdAt = "",
        updatedAt = "",
        account = account(1),
        category = category(9)
    ),
    TransactionResponse(
        id = 6,
        amount = "38000",
        transactionDate = "2026-07-05T10:00:00",
        comment = "Сдача квартиры",
        createdAt = "",
        updatedAt = "",
        account = account(2),
        category = category(10)
    )
)
