package com.example.fintracker.presentation.screens.accounts.mapper

import com.example.fintracker.domain.Account
import com.example.fintracker.presentation.screens.accounts.model.AccountUi
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import java.math.BigDecimal
import java.text.DecimalFormat

private const val DEFAULT_ACCOUNT_EMOJI = "🏦"

private val accountEmojiById = mapOf(
    1 to "💳",
    2 to "🏦",
    3 to "🏦"
)

internal fun List<Account>.toAccountsContent(): AccountsContent = AccountsContent(
    totalBalance = sumOf { it.balance.toBigDecimal() }.formatMoney(),
    accounts = map(Account::toUi)
)

private fun Account.toUi(): AccountUi = AccountUi(
    id = id,
    emoji = accountEmojiById[id] ?: DEFAULT_ACCOUNT_EMOJI,
    name = name,
    balance = balance.toBigDecimal().formatMoney()
)

private fun BigDecimal.formatMoney(): String =
    "${DecimalFormat("#,###").format(this).replace(',', ' ')} ₽"
