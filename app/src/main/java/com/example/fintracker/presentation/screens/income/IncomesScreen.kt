package com.example.fintracker.presentation.screens.income

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
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.TransactionResponse
import com.example.fintracker.presentation.screens.common.HeroSection
import com.example.fintracker.presentation.screens.common.ListItem
import com.example.fintracker.presentation.screens.common.MainCommonScreen
import com.example.fintracker.presentation.theme.FinTrackerTheme

@Composable
fun IncomesScreen(
    viewModel: IncomesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    MainCommonScreen {
        IncomesScreenContent(state)
    }

}

@Composable
fun IncomesScreenContent(
    state: IncomesUiState
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

            state.incomes.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Доходов пока нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                HeroSection(
                    label = "доходы, всего",
                    amount = state.totalAmount
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.incomes, key = { it.id }) { transaction ->
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
fun IncomesScreenPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = IncomesUiState(
                totalAmount = "23 500 ₽",
                incomes = listOf(
                    TransactionResponse(
                        id = 1,
                        account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
                        category = Category(9, "Продажа старой мебели", "🛋", isIncome = true),
                        amount = "8500",
                        transactionDate = "2025-07-10T10:00:00",
                        comment = null,
                        createdAt = "",
                        updatedAt = ""
                    ),
                    TransactionResponse(
                        id = 2,
                        account = AccountBrief(2, "Газпромбанк", "122322", "RUB"),
                        category = Category(10, "Возврат налога", "📋", isIncome = true),
                        amount = "15000",
                        transactionDate = "2025-07-09T09:00:00",
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
fun IncomesScreenLoadingPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = IncomesUiState(isLoading = true)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IncomesScreenEmptyPreview() {
    FinTrackerTheme {
        IncomesScreenContent(
            state = IncomesUiState()
        )
    }
}
