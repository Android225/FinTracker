package com.example.fintracker.presentation.screens.income.model

sealed interface IncomesEvent {
    data object Refresh : IncomesEvent
}
