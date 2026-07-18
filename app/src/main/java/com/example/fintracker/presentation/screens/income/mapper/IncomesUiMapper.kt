package com.example.fintracker.presentation.screens.income.mapper

import com.example.fintracker.domain.TransactionResponse
import com.example.fintracker.presentation.screens.income.model.IncomeTransactionUi
import com.example.fintracker.presentation.screens.income.model.IncomesContent
import java.math.BigDecimal
import java.text.DecimalFormat

internal fun List<TransactionResponse>.toIncomesContent(): IncomesContent = IncomesContent(
    totalAmount = sumOf { it.amount.toBigDecimal() }.formatMoney(),
    incomes = map { transaction ->
        IncomeTransactionUi(
            id = transaction.id,
            emoji = transaction.category.emoji,
            title = transaction.category.name,
            amount = transaction.amount.toBigDecimal().formatMoney()
        )
    }
)

private fun BigDecimal.formatMoney(): String =
    "${DecimalFormat("#,###").format(this).replace(',', ' ')} ₽"
