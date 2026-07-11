package com.example.fintracker.presentation.screens.expenses

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.screens.common.HeroSection
import com.example.fintracker.presentation.screens.common.ListItem
import com.example.fintracker.presentation.screens.common.MainCommonScreen
import com.example.fintracker.presentation.theme.FinTrackerTheme

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = viewModel()
){
    val state by viewModel.uiState.collectAsState()
    MainCommonScreen{
        ExpensesScreenContent(state)
    }
}
@Composable
fun ExpensesScreenContent(
    state: ExpensesUiState
){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp)
    ) {
        item(key = "hero") {
            HeroSection(
                label = "расходы, всего",
                amount = state.totalAmount
            )
        }

        item(key = "divider") {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }

        items(state.expenses, key = { it.id }) { transaction ->
            ListItem(
                emoji = transaction.category.emoji,
                title = transaction.category.name,
                amount = "${transaction.amount} ₽"
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 48.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            )
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