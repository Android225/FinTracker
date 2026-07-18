package com.example.fintracker.presentation.screens.income.model

data class IncomesContent(
    val totalAmount: String,
    val incomes: List<IncomeTransactionUi>
)

data class IncomeTransactionUi(
    val id: Int,
    val emoji: String,
    val title: String,
    val amount: String
)
