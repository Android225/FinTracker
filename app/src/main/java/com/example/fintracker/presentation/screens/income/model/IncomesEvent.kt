package com.example.fintracker.presentation.screens.income.model

// Пользовательские события экрана доходов.
sealed interface IncomesEvent {
    // Проверяет свежесть данных при возвращении на экран.
    data object ScreenVisible : IncomesEvent
    // Принудительно обновляет данные после свайпа, retry или изменения транзакции.
    data object Refresh : IncomesEvent
}
