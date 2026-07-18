package com.example.fintracker.presentation.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.data.mock.accounts.mockAccounts
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.accounts.mapper.toAccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AccountsContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<AccountsContent>> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadAccounts()
    }

    fun onEvent(event: AccountsEvent) {
        when (event) {
            AccountsEvent.Refresh -> loadAccounts()
        }
    }

    private fun loadAccounts() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(MOCK_LOADING_DELAY_MS)
            _uiState.value = if (mockAccounts.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Content(mockAccounts.toAccountsContent())
            }
        }
    }

    private companion object {
        const val MOCK_LOADING_DELAY_MS = 3_000L
    }
}
