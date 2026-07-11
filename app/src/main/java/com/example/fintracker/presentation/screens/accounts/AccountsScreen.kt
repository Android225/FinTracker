package com.example.fintracker.presentation.screens.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.example.fintracker.presentation.screens.expenses.ExpensesScreenContent
import com.example.fintracker.presentation.theme.FinTrackerTheme

@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    MainCommonScreen {
        AccountsScreenContent(state)
    }

}

@Composable
fun AccountsScreenContent(
    state: AccountsUiState
) {
    Column(

    ) {
        HeroSection(
            label = "баланс, всего",
            amount = state.totalBalance
        )
        LazyColumn(

            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp)
        ) {
            items(state.accounts, key = { it.id }) { account ->
                ListItem(
                    emoji = state.accountEmoji[account.id] ?: "🏦",
                    title = account.name,
                    amount = "${account.balance} ₽"
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AccountsScreenPreview() {
    FinTrackerTheme {
        AccountsScreen()
    }
}