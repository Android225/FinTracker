package com.example.fintracker.presentation.screens.income

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
import com.example.fintracker.presentation.screens.income.model.IncomeTransactionUi
import com.example.fintracker.presentation.screens.income.model.IncomesContent
import com.example.fintracker.presentation.screens.income.model.IncomesEvent
import com.example.fintracker.presentation.theme.FinTrackerTheme

// Подключает ViewModel и общий каркас экрана доходов.
@Composable
fun IncomesScreen(
    viewModel: IncomesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    MainScreenScaffold {
        IncomesScreenContent(
            state = state,
            onRetryClick = { viewModel.onEvent(IncomesEvent.Refresh) }
        )
    }

}

// Отображает экран доходов для текущего состояния.
@Composable
fun IncomesScreenContent(
    state: UiState<IncomesContent>,
    onRetryClick: () -> Unit
) {
    Column() {
        when (state) {
            UiState.Loading -> LoadingContent()
            UiState.Empty -> EmptyContent(text = "Доходов пока нет")
            is UiState.Error -> ErrorContent(error = state.error, onRetryClick = onRetryClick)

            is UiState.Content -> {
                HeroSection(
                    label = "доходы, всего",
                    amount = state.data.totalAmount
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.data.incomes, key = { it.id }) { transaction ->
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

// Preview экрана доходов с данными.
@Preview(showBackground = true)
@Composable
fun IncomesScreenPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = UiState.Content(
                IncomesContent(
                    totalAmount = "23 500 ₽",
                    incomes = listOf(
                        IncomeTransactionUi(
                            id = 1,
                            emoji = "🛋",
                            title = "Продажа старой мебели",
                            amount = "8 500 ₽"
                        ),
                        IncomeTransactionUi(
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

// Preview состояния загрузки доходов.
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

// Preview пустого состояния доходов.
@Preview(showBackground = true)
@Composable
fun IncomesScreenEmptyPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = UiState.Empty,
            onRetryClick = {}
        )
    }
}

// Preview ошибки загрузки доходов.
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
