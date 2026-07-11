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
        HeroSection(
            label = "расходы, всего",
            amount = state.totalAmount
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp)
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

@Preview(showBackground = true)
@Composable
fun ExpensesScreenPreview() {
    FinTrackerTheme {
        ExpensesScreen()
    }
}