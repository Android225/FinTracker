package com.example.fintracker.data.mock.accounts

import com.example.fintracker.domain.Account
import java.math.BigDecimal
import java.time.Instant

internal val mockAccounts = listOf(
    account(1, "Яндекс Pay", "💳", "123322"),
    account(2, "Газпромбанк", "🏦", "122322"),
    account(3, "Сбербанк", "🏦", "122322")
)

private fun account(id: Int, name: String, emoji: String, balance: String): Account = Account(
    id = id,
    userId = 1,
    name = name,
    emoji = emoji,
    balance = BigDecimal(balance),
    currency = "RUB",
    createdAt = Instant.EPOCH,
    updatedAt = Instant.EPOCH
)
