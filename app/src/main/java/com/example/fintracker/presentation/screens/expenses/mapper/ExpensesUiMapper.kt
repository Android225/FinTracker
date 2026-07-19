package com.example.fintracker.presentation.screens.expenses.mapper

import com.example.fintracker.domain.TransactionResponse
import com.example.fintracker.presentation.screens.expenses.model.ExpenseTransactionUi
import com.example.fintracker.presentation.screens.expenses.model.ExpensesContent
import java.math.BigDecimal
import java.text.DecimalFormat

// Преобразует расходные операции в содержимое экрана.
internal fun List<TransactionResponse>.toExpensesContent(): ExpensesContent = ExpensesContent(
    totalAmount = sumOf { it.amount.toBigDecimal() }.formatMoney(),
    expenses = map { transaction ->
        ExpenseTransactionUi(
            id = transaction.id,
            emoji = transaction.category.emoji,
            title = transaction.category.name,
            amount = transaction.amount.toBigDecimal().formatMoney()
        )
    }
)

// Форматирует денежную сумму для интерфейса.
private fun BigDecimal.formatMoney(): String =
    "${DecimalFormat("#,###").format(this).replace(',', ' ')} ₽"
