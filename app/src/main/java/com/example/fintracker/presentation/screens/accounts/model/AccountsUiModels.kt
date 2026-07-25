package com.example.fintracker.presentation.screens.accounts.model

data class AccountsContent(
    val totalBalance: String,
    val accounts: List<AccountUi>
)

data class AccountUi(
    val id: Long,
    val emoji: String,
    val name: String,
    val balance: String
)
