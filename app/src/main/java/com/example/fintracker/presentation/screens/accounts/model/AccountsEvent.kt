package com.example.fintracker.presentation.screens.accounts.model

// Пользовательские события экрана счетов.
sealed interface AccountsEvent {
    data object Refresh : AccountsEvent
}
