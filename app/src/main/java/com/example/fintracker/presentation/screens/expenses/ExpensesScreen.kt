package com.example.fintracker.presentation.screens.expenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.presentation.common.components.feedback.EmptyContent
import com.example.fintracker.presentation.common.components.feedback.ErrorSnackbarEffect
import com.example.fintracker.presentation.common.components.feedback.ErrorContent
import com.example.fintracker.presentation.common.components.feedback.LoadingContent
import com.example.fintracker.presentation.common.components.list.ListItem
import com.example.fintracker.presentation.common.components.refresh.RefreshableContent
import com.example.fintracker.presentation.common.components.refresh.RefreshOnResumeEffect
import com.example.fintracker.presentation.common.components.scaffold.HeroSection
import com.example.fintracker.presentation.common.components.scaffold.MainScreenScaffold
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.expenses.model.ExpenseTransactionUi
import com.example.fintracker.presentation.screens.expenses.model.ExpensesContent
import com.example.fintracker.presentation.screens.expenses.model.ExpensesEvent
import com.example.fintracker.presentation.theme.FinTrackerTheme

// Подключает ViewModel и общий каркас экрана расходов.
@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val retry = { viewModel.onEvent(ExpensesEvent.Refresh) }

    ErrorSnackbarEffect(
        effects = viewModel.effects,
        snackbarHostState = snackbarHostState,
        onRetry = retry
    )
    RefreshOnResumeEffect {
        viewModel.onEvent(ExpensesEvent.ScreenVisible)
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

// Отображает экран расходов для текущего состояния.
@Composable
fun ExpensesScreenContent(
    state: UiState<ExpensesContent>,
    onRetryClick: () -> Unit
) {
    Column() {
        when (state) {
            UiState.Loading -> LoadingContent()
            is UiState.Empty -> EmptyContent(text = "Расходов пока нет")
            is UiState.Error -> ErrorContent(error = state.error, onRetryClick = onRetryClick)

            is UiState.Content -> {
                HeroSection(
                    label = "расходы, всего",
                    amount = state.data.totalAmount
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.data.expenses, key = { it.id }) { transaction ->
                        ListItem(
                            emoji = transaction.emoji,
                            title = transaction.title,
                            amount = transaction.amount
                        )
                    }
                }
            }
        }
    }
}

// Preview экрана расходов с данными.
@Preview(showBackground = true)
@Composable
fun ExpensesScreenPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = UiState.Content(
                ExpensesContent(
                    totalAmount = "1 950 ₽",
                    expenses = listOf(
                        ExpenseTransactionUi(
                            id = 1,
                            emoji = "✏️",
                            title = "Покупка канцтоваров",
                            amount = "1 200 ₽"
                        ),
                        ExpenseTransactionUi(
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

// Preview состояния загрузки расходов.
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

// Preview пустого состояния расходов.
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

// Preview ошибки загрузки расходов.
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
