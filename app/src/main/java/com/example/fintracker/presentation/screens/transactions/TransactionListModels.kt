package com.example.fintracker.presentation.screens.transactions

import com.example.fintracker.presentation.common.format.MoneyTotalUi

data class TransactionListContent(
    val totalAmounts: List<MoneyTotalUi>,
    val transactions: List<TransactionItemUi>
)

data class TransactionItemUi(
    val id: Long,
    val emoji: String,
    val title: String,
    val amount: String,
    val syncPending: Boolean = false
)
