package com.example.fintracker.presentation.screens.accounts.mapper

import com.example.fintracker.domain.Account
import com.example.fintracker.presentation.common.format.formatMoney
import com.example.fintracker.presentation.screens.accounts.model.AccountUi
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent

// Преобразует список счетов в содержимое экрана.
internal fun List<Account>.toAccountsContent(): AccountsContent = AccountsContent(
    totalBalance = sumOf(Account::balance).formatMoney(),
    accounts = map(Account::toUi)
)

// Преобразует счёт в отображаемую модель.
private fun Account.toUi(): AccountUi = AccountUi(
    id = id,
    emoji = emoji,
    name = name,
    balance = balance.formatMoney()
)
