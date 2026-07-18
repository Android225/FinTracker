package com.example.fintracker.presentation.screens.expenses.model

sealed interface ExpensesEvent {
    data object Refresh : ExpensesEvent
}
