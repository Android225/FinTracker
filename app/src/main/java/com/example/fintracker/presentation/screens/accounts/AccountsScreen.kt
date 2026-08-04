package com.example.fintracker.presentation.screens.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fintracker.R
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.presentation.common.components.feedback.EmptyContent
import com.example.fintracker.presentation.common.components.feedback.ErrorSnackbarEffect
import com.example.fintracker.presentation.common.components.feedback.ErrorContent
import com.example.fintracker.presentation.common.components.feedback.LoadingContent
import com.example.fintracker.presentation.common.components.list.ListItem
import com.example.fintracker.presentation.common.components.list.SwipeToDeleteItem
import com.example.fintracker.presentation.common.components.refresh.RefreshableContent
import com.example.fintracker.presentation.common.components.refresh.RefreshOnResumeEffect
import com.example.fintracker.presentation.common.components.scaffold.HeroSection
import com.example.fintracker.presentation.common.components.scaffold.MainScreenScaffold
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.common.format.MoneyTotalUi
import com.example.fintracker.presentation.screens.accounts.model.AccountUi
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsEvent
import com.example.fintracker.presentation.screens.editor.AccountEditorHost
import com.example.fintracker.presentation.screens.editor.AccountEditorViewModel
import com.example.fintracker.presentation.theme.FinTrackerTheme

@Composable
fun AccountsScreen(
    onSettingsClick: () -> Unit = {},
    viewModel: AccountsViewModel = hiltViewModel(),
    editorViewModel: AccountEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val retry = { viewModel.onEvent(AccountsEvent.Refresh) }

    ErrorSnackbarEffect(
        errors = viewModel.errors,
        snackbarHostState = snackbarHostState,
        onRetry = retry
    )
    RefreshOnResumeEffect {
        viewModel.onEvent(AccountsEvent.ScreenVisible)
    }

    MainScreenScaffold(
        selectedDate = viewModel.selectedDate,
        snackbarHostState = snackbarHostState,
        onAddClick = { editorViewModel.open(null) },
        onSettingsClick = onSettingsClick
    ) {
        RefreshableContent(
            isRefreshing = state.isRefreshing,
            onRefresh = retry
        ) {
            AccountsScreenContent(
                state = state.loadState,
                onRetryClick = retry,
                onAccountClick = editorViewModel::open,
                onAccountDelete = { viewModel.onEvent(AccountsEvent.Delete(it)) }
            )
        }
    }
    AccountEditorHost(
        viewModel = editorViewModel,
        onSaved = { viewModel.onEvent(AccountsEvent.LocalDataChanged) }
    )
}

@Composable
fun AccountsScreenContent(
    state: UiState<AccountsContent>,
    onRetryClick: () -> Unit,
    onAccountClick: (Long) -> Unit = {},
    onAccountDelete: (Long) -> Unit = {}
) {
    Column(

    ) {
        when (state) {
            UiState.Loading -> LoadingContent()
            is UiState.Empty -> EmptyContent(text = stringResource(R.string.accounts_not_exist))
            is UiState.Error -> ErrorContent(error = state.error, onRetryClick = onRetryClick)

            is UiState.Content -> {
                HeroSection(
                    label = stringResource(R.string.balance_all),
                    amounts = state.data.totalBalances
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.data.accounts, key = { it.id }) { account ->
                        SwipeToDeleteItem(
                            confirmationTitle = stringResource(R.string.delete_account_title),
                            confirmationMessage = stringResource(R.string.delete_account_message),
                            onDelete = { onAccountDelete(account.id) }
                        ) {
                            ListItem(
                                emoji = account.emoji,
                                title = account.name,
                                amount = account.balance,
                                supportingText = if (account.syncPending) {
                                    stringResource(R.string.pending_sync)
                                } else {
                                    null
                                },
                                modifier = Modifier.clickable { onAccountClick(account.id) }
                            )
                        }
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
            state = UiState.Content(
                AccountsContent(
                    totalBalances = listOf(MoneyTotalUi("RUB", "245 644 ₽")),
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

@Preview(showBackground = true)
@Composable
fun AccountsScreenEmptyPreview() {
    FinTrackerTheme {
        AccountsScreenContent(
            state = UiState.Empty(),
            onRetryClick = {}
        )
    }
}

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
