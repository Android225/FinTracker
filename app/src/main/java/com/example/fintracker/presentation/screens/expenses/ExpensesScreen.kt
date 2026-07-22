package com.example.fintracker.presentation.screens.expenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
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
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.TransactionResponse
import com.example.fintracker.presentation.screens.common.HeroSection
import com.example.fintracker.presentation.screens.common.ListItem
import com.example.fintracker.presentation.screens.common.MainCommonScreen
import com.example.fintracker.presentation.theme.FinTrackerTheme

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    MainCommonScreen {
        ExpensesScreenContent(state)
    }
}

@Composable
fun ExpensesScreenContent(
    state: ExpensesUiState
) {
    Column() {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.expenses.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Расходов пока нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                HeroSection(
                    label = "расходы, всего",
                    amount = state.totalAmount
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.expenses, key = { it.id }) { transaction ->
                        ListItem(
                            emoji = transaction.category.emoji,
                            title = transaction.category.name,
                            amount = "${transaction.amount} ₽"
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
            state = ExpensesUiState(
                totalAmount = "1 950 ₽",
                expenses = listOf(
                    TransactionResponse(
                        id = 1,
                        account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
                        category = Category(1, "Покупка канцтоваров", "✏️", isIncome = false),
                        amount = "1200",
                        transactionDate = "2025-07-10T12:00:00",
                        comment = null,
                        createdAt = "",
                        updatedAt = ""
                    ),
                    TransactionResponse(
                        id = 2,
                        account = AccountBrief(2, "Газпромбанк", "122322", "RUB"),
                        category = Category(2, "Обед в кафе", "☕", isIncome = false),
                        amount = "750",
                        transactionDate = "2025-07-10T13:00:00",
                        comment = null,
                        createdAt = "",
                        updatedAt = ""
                    )
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenLoadingPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = ExpensesUiState(isLoading = true)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenEmptyPreview() {
    FinTrackerTheme {
        ExpensesScreenContent(
            state = ExpensesUiState()
        )
    }
}
