package com.example.fintracker.presentation.screens.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.presentation.common.components.feedback.EmptyContent
import com.example.fintracker.presentation.common.components.feedback.ErrorContent
import com.example.fintracker.presentation.common.components.feedback.LoadingContent
import com.example.fintracker.presentation.common.components.list.ListItem
import com.example.fintracker.presentation.common.components.scaffold.HeroSection
import com.example.fintracker.presentation.common.components.scaffold.MainScreenScaffold
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.accounts.model.AccountUi
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsEvent
import com.example.fintracker.presentation.theme.FinTrackerTheme

// Подключает ViewModel и общий каркас экрана счетов.
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    MainScreenScaffold {
        AccountsScreenContent(
            state = state,
            onRetryClick = { viewModel.onEvent(AccountsEvent.Refresh) }
        )
    }

}

// Отображает экран счетов для текущего состояния.
@Composable
fun AccountsScreenContent(
    state: UiState<AccountsContent>,
    onRetryClick: () -> Unit
) {
    Column(

    ) {
        when (state) {
            UiState.Loading -> LoadingContent()
            UiState.Empty -> EmptyContent(text = "Счетов пока нет")
            is UiState.Error -> ErrorContent(error = state.error, onRetryClick = onRetryClick)

            is UiState.Content -> {
                HeroSection(
                    label = "баланс, всего",
                    amount = state.data.totalBalance
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.data.accounts, key = { it.id }) { account ->
                        ListItem(
                            emoji = account.emoji,
                            title = account.name,
                            amount = account.balance
                        )
                    }
                }
            }
        }
    }
}

// Preview экрана счетов с данными.
@Preview(showBackground = true)
@Composable
fun AccountsScreenPreview() {
    FinTrackerTheme {
        AccountsScreenContent(
            state = UiState.Content(
                AccountsContent(
                    totalBalance = "245 644 ₽",
                    accounts = listOf(
                        AccountUi(
                            id = 1,
                            emoji = "💳",
                            name = "Яндекс Pay",
                            balance = "123 322 ₽"
                        ),
                        AccountUi(
                            id = 2,
                            emoji = "🏦",
                            name = "Газпромбанк",
                            balance = "122 322 ₽"
                        )
                    )
                )
            ),
            onRetryClick = {}
        )
    }
}

// Preview состояния загрузки счетов.
@Preview(showBackground = true)
@Composable
fun AccountsScreenLoadingPreview() {
    FinTrackerTheme {
        AccountsScreenContent(
            state = UiState.Loading,
            onRetryClick = {}
        )
    }
}

// Preview пустого состояния счетов.
@Preview(showBackground = true)
@Composable
fun AccountsScreenEmptyPreview() {
    FinTrackerTheme {
        AccountsScreenContent(
            state = UiState.Empty,
            onRetryClick = {}
        )
    }
}

// Preview ошибки загрузки счетов.
@Preview(showBackground = true)
@Composable
fun AccountsScreenErrorPreview() {
    FinTrackerTheme {
        AccountsScreenContent(
            state = UiState.Error(AppError.Network),
            onRetryClick = {}
        )
    }
}
