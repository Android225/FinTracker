package com.example.fintracker.presentation.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.domain.Account
import com.example.fintracker.presentation.common.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AccountsContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<AccountsContent>> = _uiState.asStateFlow()

    // Account from Swagger не содержит emoji, поэтому это временная UI-деталь для моков.
    private val accountEmojiById: Map<Int, String> = mapOf(
        1 to "💳",
        2 to "🏦",
        3 to "🏦"
    )

    private val mockAccounts = listOf(
        Account(id = 1, userId = 1, name = "Яндекс Pay", balance = "123322", currency = "RUB", createdAt = "", updatedAt = ""),
        Account(id = 2, userId = 1, name = "Газпромбанк", balance = "122322", currency = "RUB", createdAt = "", updatedAt = ""),
        Account(id = 3, userId = 1, name = "Сбербанк", balance = "122322", currency = "RUB", createdAt = "", updatedAt = "")
    )

    init {
        loadAccounts()
    }

    fun onEvent(event: AccountsEvent) {
        when (event) {
            AccountsEvent.Refresh -> loadAccounts()
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(3000)
            _uiState.value = if (mockAccounts.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Content(
                    AccountsContent(
                        totalBalance = "1 322 444 ₽",
                        accounts = mockAccounts.map { account -> account.toUi() }
                    )
                )
            }
        }
    }

    private fun Account.toUi(): AccountUi {
        return AccountUi(
            id = id,
            emoji = accountEmojiById[id] ?: DEFAULT_ACCOUNT_EMOJI,
            name = name,
            balance = "$balance ₽"
        )
    }

    private companion object {
        const val DEFAULT_ACCOUNT_EMOJI = "🏦"
    }
}

data class AccountsContent(
    val totalBalance: String,
    val accounts: List<AccountUi>
)

data class AccountUi(
    val id: Int,
    val emoji: String,
    val name: String,
    val balance: String
)

sealed interface AccountsEvent {
    data object Refresh : AccountsEvent
}
