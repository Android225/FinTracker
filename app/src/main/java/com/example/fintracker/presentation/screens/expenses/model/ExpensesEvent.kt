package com.example.fintracker.presentation.screens.expenses.model

// Пользовательские события экрана расходов.
sealed interface ExpensesEvent {
    // Проверяет свежесть данных при возвращении на экран.
    data object ScreenVisible : ExpensesEvent
    // Принудительно обновляет данные после свайпа, retry или изменения транзакции.
    data object Refresh : ExpensesEvent
}
