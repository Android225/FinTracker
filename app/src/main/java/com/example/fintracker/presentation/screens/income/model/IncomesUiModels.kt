package com.example.fintracker.presentation.screens.income.model

// Данные, необходимые экрану доходов.
data class IncomesContent(
    val totalAmount: String,
    val incomes: List<IncomeTransactionUi>
)

// Отображаемая модель доходной операции.
data class IncomeTransactionUi(
    val id: Int,
    val emoji: String,
    val title: String,
    val amount: String
)
