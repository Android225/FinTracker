package com.example.fintracker.presentation.screens.transactions

data class TransactionListContent(
    val totalAmount: String,
    val transactions: List<TransactionItemUi>
)

data class TransactionItemUi(
    val id: Long,
    val emoji: String,
    val title: String,
    val amount: String
)
