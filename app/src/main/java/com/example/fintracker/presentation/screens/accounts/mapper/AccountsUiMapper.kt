package com.example.fintracker.presentation.screens.accounts.mapper

import com.example.fintracker.domain.Account
import com.example.fintracker.presentation.screens.accounts.model.AccountUi
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import java.math.BigDecimal
import java.text.DecimalFormat

private const val DEFAULT_ACCOUNT_EMOJI = "🏦"

// Сопоставление тестовых счетов с их эмодзи.
private val accountEmojiById = mapOf(
    1 to "💳",
    2 to "🏦",
    3 to "🏦"
)

// Преобразует список счетов в содержимое экрана.
internal fun List<Account>.toAccountsContent(): AccountsContent = AccountsContent(
    totalBalance = sumOf { it.balance.toBigDecimal() }.formatMoney(),
    accounts = map(Account::toUi)
)

// Преобразует счёт в отображаемую модель.
private fun Account.toUi(): AccountUi = AccountUi(
    id = id,
    emoji = accountEmojiById[id] ?: DEFAULT_ACCOUNT_EMOJI,
    name = name,
    balance = balance.toBigDecimal().formatMoney()
)

// Форматирует денежную сумму для интерфейса.
private fun BigDecimal.formatMoney(): String =
    "${DecimalFormat("#,###").format(this).replace(',', ' ')} ₽"
