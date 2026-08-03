package com.example.fintracker.presentation.screens.accounts.mapper

import com.example.fintracker.domain.Account
import com.example.fintracker.presentation.common.format.formatMoney
import com.example.fintracker.presentation.common.format.moneyTotalsByCurrency
import com.example.fintracker.presentation.screens.accounts.model.AccountUi
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent

internal fun List<Account>.toAccountsContent(): AccountsContent = AccountsContent(
    totalBalances = moneyTotalsByCurrency(Account::balance, Account::currency),
    accounts = map(Account::toUi)
)

private fun Account.toUi(): AccountUi = AccountUi(
    id = id,
    emoji = emoji,
    name = name,
    balance = balance.formatMoney(currency),
    syncPending = syncPending
)
