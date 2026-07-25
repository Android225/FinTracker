package com.example.fintracker.presentation.screens.accounts.model

sealed interface AccountsEvent {
    data object ScreenVisible : AccountsEvent
    data object Refresh : AccountsEvent
    data object LocalDataChanged : AccountsEvent
}
