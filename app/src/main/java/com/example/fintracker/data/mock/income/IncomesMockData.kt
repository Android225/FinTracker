package com.example.fintracker.data.mock.income

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.TransactionResponse

// Тестовые доходные операции.
internal val mockIncomes = listOf(
    income(1, "8500", "2025-07-10T10:00:00", 1, 9, "Продажа старой мебели", "🛋", null),
    income(2, "15000", "2025-07-09T09:00:00", 2, 10, "Возврат налога", "📋", null),
    income(3, "25000", "2025-07-08T12:00:00", 1, 11, "Премия за проект", "💼", "За проект Q4"),
    income(4, "13450", "2025-07-07T14:00:00", 3, 12, "Подработка фриланс", "💻", null),
    income(5, "38000", "2025-07-05T08:00:00", 2, 13, "Сдача квартиры", "🏠", "За июнь"),
    income(6, "1200", "2025-07-04T11:00:00", 1, 14, "Кэшбэк на карту", "💳", null),
    income(7, "5000", "2025-07-03T16:00:00", 3, 15, "Подарок от родителей", "🎁", null)
)

// Создаёт тестовую доходную операцию.
private fun income(
    id: Int,
    amount: String,
    transactionDate: String,
    accountId: Int,
    categoryId: Int,
    categoryName: String,
    emoji: String,
    comment: String?
): TransactionResponse = TransactionResponse(
    id = id,
    amount = amount,
    transactionDate = transactionDate,
    comment = comment,
    createdAt = "",
    updatedAt = "",
    account = account(accountId),
    category = Category(categoryId, categoryName, emoji, isIncome = true)
)

// Возвращает тестовый счёт по идентификатору.
private fun account(id: Int): AccountBrief = when (id) {
    1 -> AccountBrief(1, "Яндекс Pay", "123322", "RUB")
    2 -> AccountBrief(2, "Газпромбанк", "122322", "RUB")
    else -> AccountBrief(3, "Сбербанк", "122322", "RUB")
}
