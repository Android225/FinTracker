package com.example.fintracker.presentation.screens.accounts.model

// Пользовательские события экрана счетов.
sealed interface AccountsEvent {
    // Проверяет свежесть данных при возвращении на экран.
    data object ScreenVisible : AccountsEvent
    // Принудительно обновляет данные после свайпа, retry или будущего изменения счёта.
    data object Refresh : AccountsEvent
}
