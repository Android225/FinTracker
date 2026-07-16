package com.example.fintracker.presentation.screens.accounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fintracker.domain.Account
import com.example.fintracker.presentation.screens.common.HeroSection
import com.example.fintracker.presentation.screens.common.ListItem
import com.example.fintracker.presentation.screens.common.MainCommonScreen
import com.example.fintracker.presentation.theme.FinTrackerTheme

@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    MainCommonScreen {
        AccountsScreenContent(state)
    }

}

@Composable
fun AccountsScreenContent(
    state: AccountsUiState
) {
    Column(

    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.accounts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Счетов пока нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                HeroSection(
                    label = "баланс, всего",
                    amount = state.totalBalance
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.accounts, key = { it.id }) { account ->
                        ListItem(
                            emoji = state.accountEmoji[account.id] ?: "🏦",
                            title = account.name,
                            amount = "${account.balance} ₽"
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AccountsScreenPreview() {
    FinTrackerTheme {
        AccountsScreenContent(
            state = AccountsUiState(
                totalBalance = "245 644 ₽",
                accounts = listOf(
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
                    )
                ),
                accountEmoji = mapOf(1 to "💳", 2 to "🏦")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountsScreenLoadingPreview() {
    FinTrackerTheme {
        AccountsScreenContent(
            state = AccountsUiState(isLoading = true)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountsScreenEmptyPreview() {
    FinTrackerTheme {
        AccountsScreenContent(
            state = AccountsUiState()
        )
    }
}
