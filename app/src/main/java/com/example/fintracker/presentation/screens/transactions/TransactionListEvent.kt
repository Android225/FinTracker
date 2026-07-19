package com.example.fintracker.presentation.screens.transactions

sealed interface TransactionListEvent {
    data object ScreenVisible : TransactionListEvent
    data object Refresh : TransactionListEvent
}
