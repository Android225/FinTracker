package com.example.fintracker.presentation.screens.transactions

import com.example.fintracker.domain.Transaction
import com.example.fintracker.presentation.common.format.formatMoney

internal fun List<Transaction>.toTransactionListContent(): TransactionListContent =
    TransactionListContent(
        totalAmount = sumOf(Transaction::amount).formatMoney(),
        transactions = map { transaction ->
            TransactionItemUi(
                id = transaction.id,
                emoji = transaction.category.emoji,
                title = transaction.comment?:transaction.category.name,
                amount = transaction.amount.formatMoney()
            )
        }
    )
