package com.example.fintracker.presentation.screens.expenses.model

data class ExpensesContent(
    val totalAmount: String,
    val expenses: List<ExpenseTransactionUi>
)

data class ExpenseTransactionUi(
    val id: Int,
    val emoji: String,
    val title: String,
    val amount: String
)
