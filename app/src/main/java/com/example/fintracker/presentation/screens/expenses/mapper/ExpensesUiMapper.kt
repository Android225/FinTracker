package com.example.fintracker.presentation.screens.expenses.mapper

import com.example.fintracker.domain.Transaction
import com.example.fintracker.presentation.screens.expenses.model.ExpenseTransactionUi
import com.example.fintracker.presentation.screens.expenses.model.ExpensesContent
import java.math.BigDecimal
import java.text.DecimalFormat

// Преобразует расходные операции в содержимое экрана.
internal fun List<Transaction>.toExpensesContent(): ExpensesContent = ExpensesContent(
    totalAmount = sumOf(Transaction::amount).formatMoney(),
    expenses = map { transaction ->
        ExpenseTransactionUi(
            id = transaction.id,
            emoji = transaction.category.emoji,
            title = transaction.category.name,
            amount = transaction.amount.formatMoney()
        )
    }
)

// Форматирует денежную сумму для интерфейса.
private fun BigDecimal.formatMoney(): String =
    "${DecimalFormat("#,###").format(this).replace(',', ' ')} ₽"
