package com.example.fintracker.presentation.screens.income.model

// Пользовательские события экрана доходов.
sealed interface IncomesEvent {
    data object Refresh : IncomesEvent
}
