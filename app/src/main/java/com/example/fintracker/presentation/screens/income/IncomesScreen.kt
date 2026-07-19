package com.example.fintracker.presentation.screens.income

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
fun IncomesScreen(
    viewModel: IncomesViewModel = hiltViewModel()
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
            IncomesScreenContent(
                state = state.dataState,
                onRetryClick = retry
            )
        }
    }

}

@Composable
fun IncomesScreenContent(
    state: UiState<TransactionListContent>,
    onRetryClick: () -> Unit
) {
    TransactionListContent(
        state = state,
        totalAmountLabel = "доходы, всего",
        emptyStateText = "Доходов пока нет",
        onRetryClick = onRetryClick
    )
}

@Preview(showBackground = true)
@Composable
fun IncomesScreenPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = UiState.Content(
                TransactionListContent(
                    totalAmount = "23 500 ₽",
                    transactions = listOf(
                        TransactionItemUi(
                            id = 1,
                            emoji = "🛋",
                            title = "Продажа старой мебели",
                            amount = "8 500 ₽"
                        ),
                        TransactionItemUi(
                            id = 2,
                            emoji = "📋",
                            title = "Возврат налога",
                            amount = "15 000 ₽"
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
fun IncomesScreenLoadingPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = UiState.Loading,
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IncomesScreenEmptyPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = UiState.Empty(),
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IncomesScreenErrorPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = UiState.Error(AppError.Network),
            onRetryClick = {}
        )
    }
}
