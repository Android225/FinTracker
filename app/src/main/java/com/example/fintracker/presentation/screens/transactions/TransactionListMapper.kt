package com.example.fintracker.presentation.screens.transactions

import com.example.fintracker.domain.Transaction
import com.example.fintracker.presentation.common.format.formatMoney
import com.example.fintracker.presentation.common.format.moneyTotalInCurrency
import java.math.BigDecimal

internal fun List<Transaction>.toTransactionListContent(
    displayCurrency: String,
    convert: (BigDecimal, String, String) -> BigDecimal
): TransactionListContent =
    TransactionListContent(
        totalAmounts = listOf(
            moneyTotalInCurrency(
                targetCurrency = displayCurrency,
                amount = Transaction::amount,
                currency = { it.account.currency },
                convert = convert
            )
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
