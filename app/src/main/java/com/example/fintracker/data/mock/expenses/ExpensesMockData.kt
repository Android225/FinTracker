package com.example.fintracker.data.mock.expenses

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import java.math.BigDecimal
import java.time.Instant

private val baseExpenses = listOf(
    expense(1, "1200", "2025-07-10T12:00:00Z", 1, 1, "Покупка канцтоваров", "✏️"),
    expense(2, "750", "2025-07-10T13:00:00Z", 2, 2, "Обед в кафе", "☕"),
    expense(3, "2300", "2025-07-09T08:00:00Z", 1, 3, "Топливо для машины", "⛽"),
    expense(4, "450", "2025-07-09T10:00:00Z", 3, 4, "Подписка на сервис", "📱"),
    expense(5, "5800", "2025-07-08T14:00:00Z", 2, 5, "Ремонт техники", "🔧", "Ремонт ноутбука"),
    expense(6, "3200", "2025-07-08T09:00:00Z", 1, 6, "Покупка билетов", "🎫"),
    expense(7, "800", "2025-07-07T11:00:00Z", 3, 7, "Оплата интернета", "🌐"),
    expense(8, "2450", "2025-07-07T16:00:00Z", 1, 8, "Магазин продуктов", "🛒")
)

internal val mockExpenses = baseExpenses + baseExpenses.map { expense ->
    expense.copy(id = expense.id + baseExpenses.size)
}

private fun expense(
    id: Int,
    amount: String,
    transactionDate: String,
    accountId: Int,
    categoryId: Int,
    categoryName: String,
    emoji: String,
    comment: String? = null
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
        category = Category(categoryId, categoryName, emoji, isIncome = false)
    )
}

private fun account(id: Int): AccountBrief = when (id) {
    1 -> AccountBrief(1, "Яндекс Pay", "💳", BigDecimal("123322"), "RUB")
    2 -> AccountBrief(2, "Газпромбанк", "🏦", BigDecimal("122322"), "RUB")
    else -> AccountBrief(3, "Сбербанк", "🏦", BigDecimal("122322"), "RUB")
}
