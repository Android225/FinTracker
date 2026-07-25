package com.example.fintracker.presentation.screens.expenses

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fintracker.R
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.presentation.common.components.feedback.ErrorSnackbarEffect
import com.example.fintracker.presentation.common.components.refresh.RefreshableContent
import com.example.fintracker.presentation.common.components.refresh.RefreshOnResumeEffect
import com.example.fintracker.presentation.common.components.scaffold.MainScreenScaffold
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.common.format.MoneyTotalUi
import com.example.fintracker.presentation.screens.transactions.TransactionItemUi
import com.example.fintracker.presentation.screens.transactions.TransactionListContent
import com.example.fintracker.presentation.screens.transactions.TransactionListEvent
import com.example.fintracker.presentation.theme.FinTrackerTheme
import com.example.fintracker.presentation.screens.editor.TransactionEditorHost
import com.example.fintracker.presentation.screens.editor.TransactionEditorViewModel

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = hiltViewModel(),
    editorViewModel: TransactionEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val retry = { viewModel.onEvent(TransactionListEvent.Refresh) }

    ErrorSnackbarEffect(
        errors = viewModel.errors,
        snackbarHostState = snackbarHostState,
        onRetry = retry
    )
    RefreshOnResumeEffect {
        viewModel.onEvent(TransactionListEvent.ScreenVisible)
    }

    MainScreenScaffold(
        selectedDate = viewModel.selectedDate,
        snackbarHostState = snackbarHostState,
        onAddClick = { editorViewModel.open(TransactionType.Expense, null) }
    ) {
        RefreshableContent(
            isRefreshing = state.isRefreshing,
            onRefresh = retry
        ) {
            ExpensesScreenContent(
                state = state.loadState,
                onRetryClick = retry,
                onTransactionClick = {
                    editorViewModel.open(TransactionType.Expense, it)
                }
            )
        }
    }
    TransactionEditorHost(
        viewModel = editorViewModel,
        onSaved = { viewModel.onEvent(TransactionListEvent.LocalDataChanged) }
    )
}

@Composable
fun ExpensesScreenContent(
    state: UiState<TransactionListContent>,
    onRetryClick: () -> Unit,
    onTransactionClick: (Long) -> Unit = {}
) {
    TransactionListContent(
        state = state,
        totalAmountLabel = stringResource(R.string.expenses_all),
        emptyStateText = stringResource(R.string.expenses_not_exist),
        onRetryClick = onRetryClick,
        onTransactionClick = onTransactionClick
    )
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = UiState.Content(
                TransactionListContent(
                    totalAmounts = listOf(MoneyTotalUi("RUB", "1 950 ₽")),
                    transactions = listOf(
                        TransactionItemUi(
                            id = 1,
                            emoji = "✏️",
                            title = "Покупка канцтоваров",
                            amount = "1 200 ₽"
                        ),
                        TransactionItemUi(
                            id = 2,
                            emoji = "☕",
                            title = "Обед в кафе",
                            amount = "750 ₽"
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
fun ExpensesScreenLoadingPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = UiState.Loading,
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenEmptyPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = UiState.Empty(),
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenErrorPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = UiState.Error(AppError.Network),
            onRetryClick = {}
        )
    }
}
