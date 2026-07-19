package com.example.fintracker.data.mock.accounts

import com.example.fintracker.domain.Account

// Тестовые счета для экрана счетов.
internal val mockAccounts = listOf(
    Account(
        id = 1,
        userId = 1,
        name = "Яндекс Pay",
        balance = "123322",
        currency = "RUB",
        createdAt = "",
        updatedAt = ""
    ),
    Account(
        id = 2,
        userId = 1,
        name = "Газпромбанк",
        balance = "122322",
        currency = "RUB",
        createdAt = "",
        updatedAt = ""
    ),
    Account(
        id = 3,
        userId = 1,
        name = "Сбербанк",
        balance = "122322",
        currency = "RUB",
        createdAt = "",
        updatedAt = ""
    )
)
