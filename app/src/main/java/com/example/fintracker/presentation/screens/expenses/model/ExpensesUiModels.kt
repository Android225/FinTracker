package com.example.fintracker.presentation.screens.expenses.model

// Данные, необходимые экрану расходов.
data class ExpensesContent(
    val totalAmount: String,
    val expenses: List<ExpenseTransactionUi>
)

// Отображаемая модель расходной операции.
data class ExpenseTransactionUi(
    val id: Int,
    val emoji: String,
    val title: String,
    val amount: String
)
