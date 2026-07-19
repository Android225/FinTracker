package com.example.fintracker.presentation.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.di.qualifier.DefaultDispatcher
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.domain.result.map
import com.example.fintracker.domain.usecase.GetTransactionsUseCase
import com.example.fintracker.presentation.common.model.ScreenUiState
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.common.model.startRefreshing
import com.example.fintracker.presentation.common.model.updateFrom
import com.example.fintracker.presentation.common.refresh.RefreshPolicy
import com.example.fintracker.presentation.screens.transactions.TransactionListContent
import com.example.fintracker.presentation.screens.transactions.TransactionListEvent
import com.example.fintracker.presentation.screens.transactions.toTransactionListContent
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
    private val getTransactions: GetTransactionsUseCase,
    clock: Clock,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    val selectedDate: LocalDate = LocalDate.now(clock)

    private val _uiState = MutableStateFlow(ScreenUiState<TransactionListContent>())
    val uiState: StateFlow<ScreenUiState<TransactionListContent>> = _uiState.asStateFlow()

    private val errorChannel = Channel<AppError>(Channel.BUFFERED)
    val errors: Flow<AppError> = errorChannel.receiveAsFlow()

    private val refreshPolicy = RefreshPolicy(clock)
    private var loadJob: Job? = null

    init {
        loadExpenses(force = true)
    }

    fun onEvent(event: TransactionListEvent) {
        when (event) {
            TransactionListEvent.ScreenVisible -> loadExpenses(force = false)
            TransactionListEvent.Refresh -> loadExpenses(force = true)
        }
    }

    // Загружает расходные транзакции за выбранный день с ограничением автообновлений.
    private fun loadExpenses(force: Boolean) {
        if (loadJob?.isActive == true || !refreshPolicy.shouldRefresh(force)) return
        loadJob = viewModelScope.launch {
            val previousState = _uiState.startRefreshing()
            val result = getTransactions(
                type = TransactionType.Expense,
                startDate = selectedDate,
                endDate = selectedDate
            )
            val uiResult = withContext(defaultDispatcher) {
                result.map { transactions -> transactions.toTransactionListContent() }
            }
            if (uiResult is DataResult.Success) {
                refreshPolicy.markSuccessfulRefresh()
            }
            _uiState.updateFrom(
                result = uiResult,
                previousState = previousState,
                isEmpty = { it.transactions.isEmpty() },
                onRefreshError = errorChannel::send
            )
        }
    }
}
