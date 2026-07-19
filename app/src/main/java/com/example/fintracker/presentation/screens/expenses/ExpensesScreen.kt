package com.example.fintracker.presentation.screens.expenses

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.presentation.common.components.feedback.ErrorSnackbarEffect
import com.example.fintracker.presentation.common.components.refresh.RefreshableContent
import com.example.fintracker.presentation.common.components.refresh.RefreshOnResumeEffect
import com.example.fintracker.presentation.common.components.scaffold.MainScreenScaffold
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.transactions.TransactionItemUi
import com.example.fintracker.presentation.screens.transactions.TransactionListContent
import com.example.fintracker.presentation.screens.transactions.TransactionListEvent
import com.example.fintracker.presentation.theme.FinTrackerTheme

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val retry = { viewModel.onEvent(TransactionListEvent.Refresh) }

    ErrorSnackbarEffect(
        effects = viewModel.effects,
        snackbarHostState = snackbarHostState,
        onRetry = retry
    )
    RefreshOnResumeEffect {
        viewModel.onEvent(TransactionListEvent.ScreenVisible)
    }

    MainScreenScaffold(
        selectedDate = viewModel.selectedDate,
        snackbarHostState = snackbarHostState
    ) {
        RefreshableContent(
            isRefreshing = state.isRefreshing,
            onRefresh = retry
        ) {
            ExpensesScreenContent(
                state = state.dataState,
                onRetryClick = retry
            )
        }
    }
}

@Composable
fun ExpensesScreenContent(
    state: UiState<TransactionListContent>,
    onRetryClick: () -> Unit
) {
    TransactionListContent(
        state = state,
        totalAmountLabel = "расходы, всего",
        emptyStateText = "Расходов пока нет",
        onRetryClick = onRetryClick
    )
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = UiState.Content(
                TransactionListContent(
                    totalAmount = "1 950 ₽",
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
