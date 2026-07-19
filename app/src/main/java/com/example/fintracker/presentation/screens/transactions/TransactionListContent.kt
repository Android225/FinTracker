package com.example.fintracker.presentation.screens.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fintracker.presentation.common.components.feedback.EmptyContent
import com.example.fintracker.presentation.common.components.feedback.ErrorContent
import com.example.fintracker.presentation.common.components.feedback.LoadingContent
import com.example.fintracker.presentation.common.components.list.ListItem
import com.example.fintracker.presentation.common.components.scaffold.HeroSection
import com.example.fintracker.presentation.common.model.UiState

@Composable
fun TransactionListContent(
    state: UiState<TransactionListContent>,
    totalAmountLabel: String,
    emptyStateText: String,
    onRetryClick: () -> Unit
) {
    Column {
        when (state) {
            UiState.Loading -> LoadingContent()
            is UiState.Empty -> EmptyContent(text = emptyStateText)
            is UiState.Error -> ErrorContent(error = state.error, onRetryClick = onRetryClick)

            is UiState.Content -> {
                HeroSection(
                    label = totalAmountLabel,
                    amount = state.data.totalAmount
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.data.transactions, key = { it.id }) { transaction ->
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
