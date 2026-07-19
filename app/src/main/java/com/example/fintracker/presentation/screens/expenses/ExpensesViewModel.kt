package com.example.fintracker.presentation.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.di.qualifier.DefaultDispatcher
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.domain.result.map
import com.example.fintracker.domain.usecase.GetTransactionsByTypeUseCase
import com.example.fintracker.presentation.common.model.ScreenEffect
import com.example.fintracker.presentation.common.model.RefreshableUiState
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.common.model.startRefreshing
import com.example.fintracker.presentation.common.model.updateFrom
import com.example.fintracker.presentation.common.refresh.RefreshPolicy
import com.example.fintracker.presentation.screens.expenses.mapper.toExpensesContent
import com.example.fintracker.presentation.screens.expenses.model.ExpensesContent
import com.example.fintracker.presentation.screens.expenses.model.ExpensesEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getTransactionsByType: GetTransactionsByTypeUseCase,
    clock: Clock,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    val selectedDate: LocalDate = LocalDate.now(clock)

    private val refreshPolicy = RefreshPolicy(clock)
    private val _uiState = MutableStateFlow(RefreshableUiState<ExpensesContent>())
    val uiState: StateFlow<RefreshableUiState<ExpensesContent>> = _uiState.asStateFlow()

    private val effectChannel = Channel<ScreenEffect>(Channel.BUFFERED)
    val effects: Flow<ScreenEffect> = effectChannel.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        loadExpenses(force = true)
    }

    fun onEvent(event: ExpensesEvent) {
        when (event) {
            ExpensesEvent.ScreenVisible -> loadExpenses(force = false)
            ExpensesEvent.Refresh -> loadExpenses(force = true)
        }
    }

    // Загружает расходные транзакции за выбранный день с ограничением автообновлений.
    private fun loadExpenses(force: Boolean) {
        if (loadJob?.isActive == true || !refreshPolicy.shouldRefresh(force)) return
        loadJob = viewModelScope.launch {
            val previousState = _uiState.startRefreshing()
            val result = getTransactionsByType(
                startDate = selectedDate,
                endDate = selectedDate,
                isIncome = false
            )
            val uiResult = withContext(defaultDispatcher) {
                result.map { transactions -> transactions.toExpensesContent() }
            }
            if (uiResult is DataResult.Success) {
                refreshPolicy.markSuccessfulRefresh()
            }
            _uiState.updateFrom(
                result = uiResult,
                previousState = previousState,
                isEmpty = { it.expenses.isEmpty() },
                onRefreshError = effectChannel::send
            )
        }
    }
}
