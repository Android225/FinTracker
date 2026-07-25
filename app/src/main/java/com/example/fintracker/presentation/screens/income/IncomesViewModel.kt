package com.example.fintracker.presentation.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.di.qualifier.DefaultDispatcher
import com.example.fintracker.data.sync.SyncCoordinator
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.domain.result.map
import com.example.fintracker.domain.usecase.GetTransactionsUseCase
import com.example.fintracker.presentation.common.model.ScreenUiState
import com.example.fintracker.presentation.common.model.finishRefreshWith
import com.example.fintracker.presentation.common.model.finishRefreshSilently
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
class IncomesViewModel @Inject constructor(
    private val getTransactions: GetTransactionsUseCase,
    private val syncCoordinator: SyncCoordinator? = null,
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

    fun onEvent(event: TransactionListEvent) {
        when (event) {
            TransactionListEvent.ScreenVisible -> loadIncomes(force = false)
            TransactionListEvent.Refresh -> loadIncomes(force = true)
            TransactionListEvent.LocalDataChanged -> loadIncomes(
                force = true,
                synchronize = false
            )
        }
    }


    // Загружает доходные транзакции за выбранный день с ограничением автообновлений.
    //TODO("Реализовать логику на экране в левом верхнем угле выбор либо всего периода отображения либо конкретной даты
    //      пока что реализация от
    // ")
    private fun loadIncomes(force: Boolean, synchronize: Boolean = true) {
        if (loadJob?.isActive == true) {
            if (!force) return
            loadJob?.cancel()
        }
        loadJob = viewModelScope.launch {
            val previousState = _uiState.startRefreshing()
            val shouldSync = synchronize && refreshPolicy.shouldRefresh(force)
            _uiState.updateFrom(
                result = readLocalIncomes(),
                previousState = previousState,
                isEmpty = { it.transactions.isEmpty() },
                onRefreshError = errorChannel::send
            )
            if (!shouldSync) return@launch
            val coordinator = syncCoordinator ?: return@launch

            _uiState.value = _uiState.value.copy(isRefreshing = true)
            when (val syncResult = coordinator.sync()) {
                is DataResult.Success -> {
                    refreshPolicy.markSuccessfulRefresh()
                    _uiState.updateFrom(
                        result = readLocalIncomes(),
                        previousState = _uiState.value.loadState,
                        isEmpty = { it.transactions.isEmpty() },
                        onRefreshError = errorChannel::send
                    )
                }
                is DataResult.Failure -> {
                    if (force) {
                        _uiState.finishRefreshWith(syncResult.error, errorChannel::send)
                    } else {
                        _uiState.finishRefreshSilently()
                    }
                }
            }
        }
    }

    private suspend fun readLocalIncomes(): DataResult<TransactionListContent> {
        val result = getTransactions(
            type = TransactionType.Income,
            startDate = LocalDate.of(1990, 1, 1),
            endDate = selectedDate
        )
        return withContext(defaultDispatcher) {
            result.map { transactions -> transactions.toTransactionListContent() }
        }
    }
}
