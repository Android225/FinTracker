package com.example.fintracker.presentation.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.data.mock.expenses.mockExpenses
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.expenses.mapper.toExpensesContent
import com.example.fintracker.presentation.screens.expenses.model.ExpensesContent
import com.example.fintracker.presentation.screens.expenses.model.ExpensesEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Управляет состоянием и загрузкой экрана расходов.
class ExpensesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ExpensesContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<ExpensesContent>> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadExpenses()
    }

    // Обрабатывает события экрана расходов.
    fun onEvent(event: ExpensesEvent) {
        when (event) {
            ExpensesEvent.Refresh -> loadExpenses()
        }
    }

    // Загружает тестовые расходы и обновляет состояние UI.
    private fun loadExpenses() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(MOCK_LOADING_DELAY_MS)
            _uiState.value = if (mockExpenses.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Content(mockExpenses.toExpensesContent())
            }
        }
    }

    private companion object {
        const val MOCK_LOADING_DELAY_MS = 3_000L
    }
}
