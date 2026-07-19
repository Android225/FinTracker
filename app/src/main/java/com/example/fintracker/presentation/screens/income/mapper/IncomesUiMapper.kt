package com.example.fintracker.presentation.screens.income.mapper

import com.example.fintracker.domain.Transaction
import com.example.fintracker.presentation.common.format.formatMoney
import com.example.fintracker.presentation.screens.income.model.IncomeTransactionUi
import com.example.fintracker.presentation.screens.income.model.IncomesContent

// Преобразует доходные операции в содержимое экрана.
internal fun List<Transaction>.toIncomesContent(): IncomesContent = IncomesContent(
    totalAmount = sumOf(Transaction::amount).formatMoney(),
    incomes = map { transaction ->
        IncomeTransactionUi(
            id = transaction.id,
            emoji = transaction.category.emoji,
            title = transaction.category.name,
            amount = transaction.amount.formatMoney()
        )
    }
)
