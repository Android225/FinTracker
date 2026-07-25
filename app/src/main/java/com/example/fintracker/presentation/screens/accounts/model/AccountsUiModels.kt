package com.example.fintracker.presentation.screens.accounts.model

import com.example.fintracker.presentation.common.format.MoneyTotalUi

data class AccountsContent(
    val totalBalances: List<MoneyTotalUi>,
    val accounts: List<AccountUi>
)

data class AccountUi(
    val id: Long,
    val emoji: String,
    val name: String,
    val balance: String,
    val syncPending: Boolean = false
)
