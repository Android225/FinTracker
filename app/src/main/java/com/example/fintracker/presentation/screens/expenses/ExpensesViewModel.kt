package com.example.fintracker.presentation.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.TransactionResponse
import com.example.fintracker.presentation.common.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpensesViewModel: ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ExpensesContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<ExpensesContent>> = _uiState.asStateFlow()

    private val mockExpenses = listOf(
        TransactionResponse(
            id = 1, amount = "1200", transactionDate = "2025-07-10T12:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(1, "Покупка канцтоваров", "✏️", isIncome = false)
        ),
        TransactionResponse(
            id = 2, amount = "750", transactionDate = "2025-07-10T13:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(2, "Газпромбанк", "122322", "RUB"),
            category = Category(2, "Обед в кафе", "☕", isIncome = false)
        ),
        TransactionResponse(
            id = 3, amount = "2300", transactionDate = "2025-07-09T08:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(3, "Топливо для машины", "⛽", isIncome = false)
        ),
        TransactionResponse(
            id = 4, amount = "450", transactionDate = "2025-07-09T10:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(3, "Сбербанк", "122322", "RUB"),
            category = Category(4, "Подписка на сервис", "📱", isIncome = false)
        ),
        TransactionResponse(
            id = 5, amount = "5800", transactionDate = "2025-07-08T14:00:00",
            comment = "Ремонт ноутбука", createdAt = "", updatedAt = "",
            account = AccountBrief(2, "Газпромбанк", "122322", "RUB"),
            category = Category(5, "Ремонт техники", "🔧", isIncome = false)
        ),
        TransactionResponse(
            id = 6, amount = "3200", transactionDate = "2025-07-08T09:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(6, "Покупка билетов", "🎫", isIncome = false)
        ),
        TransactionResponse(
            id = 7, amount = "800", transactionDate = "2025-07-07T11:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(3, "Сбербанк", "122322", "RUB"),
            category = Category(7, "Оплата интернета", "🌐", isIncome = false)
        ),
        TransactionResponse(
            id = 8, amount = "2450", transactionDate = "2025-07-07T16:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(8, "Магазин продуктов", "🛒", isIncome = false)
        ),
        TransactionResponse(
            id = 9, amount = "1200", transactionDate = "2025-07-10T12:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(1, "Покупка канцтоваров", "✏️", isIncome = false)
        ),
        TransactionResponse(
            id = 10, amount = "750", transactionDate = "2025-07-10T13:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(2, "Газпромбанк", "122322", "RUB"),
            category = Category(2, "Обед в кафе", "☕", isIncome = false)
        ),
        TransactionResponse(
            id = 11, amount = "2300", transactionDate = "2025-07-09T08:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(3, "Топливо для машины", "⛽", isIncome = false)
        ),
        TransactionResponse(
            id = 12, amount = "450", transactionDate = "2025-07-09T10:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(3, "Сбербанк", "122322", "RUB"),
            category = Category(4, "Подписка на сервис", "📱", isIncome = false)
        ),
        TransactionResponse(
            id = 13, amount = "5800", transactionDate = "2025-07-08T14:00:00",
            comment = "Ремонт ноутбука", createdAt = "", updatedAt = "",
            account = AccountBrief(2, "Газпромбанк", "122322", "RUB"),
            category = Category(5, "Ремонт техники", "🔧", isIncome = false)
        ),
        TransactionResponse(
            id = 14, amount = "3200", transactionDate = "2025-07-08T09:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(6, "Покупка билетов", "🎫", isIncome = false)
        ),
        TransactionResponse(
            id = 15, amount = "800", transactionDate = "2025-07-07T11:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(3, "Сбербанк", "122322", "RUB"),
            category = Category(7, "Оплата интернета", "🌐", isIncome = false)
        ),
        TransactionResponse(
            id = 16, amount = "2450", transactionDate = "2025-07-07T16:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(8, "Магазин продуктов", "🛒", isIncome = false)
        )
    )

    init {
        loadExpenses()
    }

    fun onEvent(event: ExpensesEvent) {
        when (event) {
            ExpensesEvent.Refresh -> loadExpenses()
        }
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(3000)
            _uiState.value = if (mockExpenses.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Content(
                    ExpensesContent(
                        totalAmount = "323 524 ₽",
                        expenses = mockExpenses.map { transaction ->
                            TransactionUi(
                                id = transaction.id,
                                emoji = transaction.category.emoji,
                                title = transaction.category.name,
                                amount = "${transaction.amount} ₽"
                            )
                        }
                    )
                )
            }
        }
    }

}

data class ExpensesContent(
    val totalAmount: String,
    val expenses: List<TransactionUi>
)

data class TransactionUi(
    val id: Int,
    val emoji: String,
    val title: String,
    val amount: String
)

sealed interface ExpensesEvent {
    data object Refresh : ExpensesEvent
}
