package com.example.fintracker.presentation.screens.income

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


class IncomesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<IncomesContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<IncomesContent>> = _uiState.asStateFlow()

    private val mockIncomes = listOf(
        TransactionResponse(
            id = 1, amount = "8500", transactionDate = "2025-07-10T10:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(9, "Продажа старой мебели", "🛋", isIncome = true)
        ),
        TransactionResponse(
            id = 2, amount = "15000", transactionDate = "2025-07-09T09:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(2, "Газпромбанк", "122322", "RUB"),
            category = Category(10, "Возврат налога", "📋", isIncome = true)
        ),
        TransactionResponse(
            id = 3, amount = "25000", transactionDate = "2025-07-08T12:00:00",
            comment = "За проект Q4", createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(11, "Премия за проект", "💼", isIncome = true)
        ),
        TransactionResponse(
            id = 4, amount = "13450", transactionDate = "2025-07-07T14:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(3, "Сбербанк", "122322", "RUB"),
            category = Category(12, "Подработка фриланс", "💻", isIncome = true)
        ),
        TransactionResponse(
            id = 5, amount = "38000", transactionDate = "2025-07-05T08:00:00",
            comment = "За июнь", createdAt = "", updatedAt = "",
            account = AccountBrief(2, "Газпромбанк", "122322", "RUB"),
            category = Category(13, "Сдача квартиры", "🏠", isIncome = true)
        ),
        TransactionResponse(
            id = 6, amount = "1200", transactionDate = "2025-07-04T11:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(1, "Яндекс Pay", "123322", "RUB"),
            category = Category(14, "Кэшбэк на карту", "💳", isIncome = true)
        ),
        TransactionResponse(
            id = 7, amount = "5000", transactionDate = "2025-07-03T16:00:00",
            comment = null, createdAt = "", updatedAt = "",
            account = AccountBrief(3, "Сбербанк", "122322", "RUB"),
            category = Category(15, "Подарок от родителей", "🎁", isIncome = true)
        )
    )

    init {
        loadIncomes()
    }

    fun onEvent(event: IncomesEvent) {
        when (event) {
            IncomesEvent.Refresh -> loadIncomes()
        }
    }

    private fun loadIncomes() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(3000)
            _uiState.value = if (mockIncomes.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Content(
                    IncomesContent(
                        totalAmount = "323 524 ₽",
                        incomes = mockIncomes.map { transaction ->
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

data class IncomesContent(
    val totalAmount: String,
    val incomes: List<TransactionUi>
)

data class TransactionUi(
    val id: Int,
    val emoji: String,
    val title: String,
    val amount: String
)

sealed interface IncomesEvent {
    data object Refresh : IncomesEvent
}
