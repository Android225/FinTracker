package com.example.fintracker.presentation.screens.accounts.model

// Данные, необходимые экрану счетов.
data class AccountsContent(
    val totalBalance: String,
    val accounts: List<AccountUi>
)

// Отображаемая модель одного счёта.
data class AccountUi(
    val id: Int,
    val emoji: String,
    val name: String,
    val balance: String
)
