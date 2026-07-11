package com.example.fintracker.presentation.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.domain.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    // Account from Swagger не содержит emoji, поэтому храним отдельно для моков
    val accountEmojis: Map<Int, String> = mapOf(
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
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    accounts = mockAccounts,
                    totalBalance = "1 322 444 ₽",
                    accountEmoji = accountEmojis
                )
            }
        }
    }
}
data class AccountsUiState(
    val totalBalance: String = "0 ₽",
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    //временное решение для моков данных счетов, т.к иконки из swagger не приходят, в следующих дз заменится
    val accountEmoji: Map<Int, String> = emptyMap()
)
sealed interface AccountsEvent {
    data object Refresh : AccountsEvent
}
