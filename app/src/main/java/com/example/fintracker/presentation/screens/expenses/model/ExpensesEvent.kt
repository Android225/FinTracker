package com.example.fintracker.presentation.screens.expenses.model

// Пользовательские события экрана расходов.
sealed interface ExpensesEvent {
    data object Refresh : ExpensesEvent
}
