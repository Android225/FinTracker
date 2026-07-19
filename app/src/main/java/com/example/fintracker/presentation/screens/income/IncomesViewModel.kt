package com.example.fintracker.presentation.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.data.mock.income.mockIncomes
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.income.mapper.toIncomesContent
import com.example.fintracker.presentation.screens.income.model.IncomesContent
import com.example.fintracker.presentation.screens.income.model.IncomesEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Управляет состоянием и загрузкой экрана доходов.
class IncomesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<IncomesContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<IncomesContent>> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadIncomes()
    }

    // Обрабатывает события экрана доходов.
    fun onEvent(event: IncomesEvent) {
        when (event) {
            IncomesEvent.Refresh -> loadIncomes()
        }
    }

    // Загружает тестовые доходы и обновляет состояние UI.
    private fun loadIncomes() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(MOCK_LOADING_DELAY_MS)
            _uiState.value = if (mockIncomes.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Content(mockIncomes.toIncomesContent())
            }
        }
    }

    private companion object {
        const val MOCK_LOADING_DELAY_MS = 3_000L
    }
}
