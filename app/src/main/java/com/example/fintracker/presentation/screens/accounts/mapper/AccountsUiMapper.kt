package com.example.fintracker.presentation.screens.accounts.mapper

import com.example.fintracker.domain.Account
import com.example.fintracker.presentation.common.format.formatMoney
import com.example.fintracker.presentation.common.format.moneyTotalInCurrency
import com.example.fintracker.presentation.screens.accounts.model.AccountUi
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import java.math.BigDecimal

internal fun List<Account>.toAccountsContent(
    displayCurrency: String,
    convert: (BigDecimal, String, String) -> BigDecimal
): AccountsContent = AccountsContent(
    totalBalances = listOf(
        moneyTotalInCurrency(
            targetCurrency = displayCurrency,
            amount = Account::balance,
            currency = Account::currency,
            convert = convert
        )
    ),
    accounts = map(Account::toUi)
)

private fun Account.toUi(): AccountUi = AccountUi(
    id = id,
    emoji = emoji,
    name = name,
    balance = balance.formatMoney(currency),
    syncPending = syncPending
)
