package com.example.fintracker.presentation.screens.expenses

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
import com.example.fintracker.domain.AppError
import com.example.fintracker.presentation.common.UiState
import com.example.fintracker.presentation.screens.common.EmptyContent
import com.example.fintracker.presentation.screens.common.ErrorContent
import com.example.fintracker.presentation.screens.common.HeroSection
import com.example.fintracker.presentation.screens.common.ListItem
import com.example.fintracker.presentation.screens.common.LoadingContent
import com.example.fintracker.presentation.screens.common.MainCommonScreen
import com.example.fintracker.presentation.theme.FinTrackerTheme

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    MainCommonScreen {
        ExpensesScreenContent(
            state = state,
            onRetryClick = { viewModel.onEvent(ExpensesEvent.Refresh) }
        )
    }
}

@Composable
fun ExpensesScreenContent(
    state: UiState<ExpensesContent>,
    onRetryClick: () -> Unit
) {
    Column() {
        when (state) {
            UiState.Loading -> LoadingContent()
            UiState.Empty -> EmptyContent(text = "Расходов пока нет")
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

@Preview(showBackground = true)
@Composable
fun ExpensesScreenPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = UiState.Content(
                ExpensesContent(
                    totalAmount = "1 950 ₽",
                    expenses = listOf(
                        TransactionUi(
                            id = 1,
                            emoji = "✏️",
                            title = "Покупка канцтоваров",
                            amount = "1 200 ₽"
                        ),
                        TransactionUi(
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
            state = UiState.Empty,
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
