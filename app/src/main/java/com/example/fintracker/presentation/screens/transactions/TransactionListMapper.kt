package com.example.fintracker.presentation.screens.transactions

import com.example.fintracker.domain.Transaction
import com.example.fintracker.presentation.common.format.formatMoney
import com.example.fintracker.presentation.common.format.moneyTotalsByCurrency

internal fun List<Transaction>.toTransactionListContent(): TransactionListContent =
    TransactionListContent(
        totalAmounts = moneyTotalsByCurrency(
            amount = Transaction::amount,
            currency = { it.account.currency }
        ),
        transactions = map { transaction ->
            TransactionItemUi(
                id = transaction.id,
                emoji = transaction.category.emoji,
                title = transaction.comment?:transaction.category.name,
                amount = transaction.amount.formatMoney(transaction.account.currency),
                syncPending = transaction.syncPending
            )
        }
    )
