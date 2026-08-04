package com.example.fintracker.presentation.screens.expenses

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.di.qualifier.DefaultDispatcher
import com.example.fintracker.data.sync.SyncCoordinator
import com.example.fintracker.data.sync.SyncScheduler
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.ExchangeRatesRepository
import com.example.fintracker.domain.repository.SettingsRepository
import com.example.fintracker.domain.repository.TransactionsRepository
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getTransactions: GetTransactionsUseCase,
    private val transactionsRepository: TransactionsRepository,
    private val settingsRepository: SettingsRepository,
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val syncCoordinator: SyncCoordinator? = null,
    @param:ApplicationContext private val context: Context,
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
    private var displayCurrency = "RUB"
    private var hasLoaded = false

    init {
        settingsRepository.settings
            .map { it.currency.code }
            .distinctUntilChanged()
            .onEach { currency ->
                val changed = displayCurrency != currency
                displayCurrency = currency
                if (changed && hasLoaded) loadExpenses(force = true, synchronize = false)
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: TransactionListEvent) {
        when (event) {
            TransactionListEvent.ScreenVisible -> loadExpenses(force = false)
            TransactionListEvent.Refresh -> loadExpenses(force = true)
            TransactionListEvent.LocalDataChanged -> loadExpenses(
                force = true,
                synchronize = false
            )
            is TransactionListEvent.Delete -> deleteTransaction(event.id)
        }
    }

    private fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            when (val result = transactionsRepository.deleteTransaction(id)) {
                is DataResult.Failure -> errorChannel.send(result.error)
                is DataResult.Success -> {
                    loadExpenses(force = true, synchronize = false)
                    val workInfo = SyncScheduler.awaitCompletion(
                        context,
                        SyncScheduler.enqueueNow(context)
                    )
                    loadExpenses(force = true, synchronize = false)
                    SyncScheduler.failureError(workInfo)?.let { errorChannel.send(it) }
                }
            }
        }
    }

    // Загружает расходные транзакции за выбранный день с ограничением автообновлений.
    //TODO("Реализовать логику на экране в левом верхнем угле выбор либо всего периода отображения либо конкретной даты
    //      пока что реализация от
    // ")
    private fun loadExpenses(force: Boolean, synchronize: Boolean = true) {
        hasLoaded = true
        if (loadJob?.isActive == true) {
            if (!force) return
            loadJob?.cancel()
        }
        loadJob = viewModelScope.launch {
            val previousState = _uiState.startRefreshing()
            val shouldSync = synchronize && refreshPolicy.shouldRefresh(force)
            _uiState.updateFrom(
                result = readLocalExpenses(),
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
                        result = readLocalExpenses(),
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

    private suspend fun readLocalExpenses(): DataResult<TransactionListContent> {
        val result = getTransactions(
            type = TransactionType.Expense,
            startDate = LocalDate.of(1990, 1, 1),
            endDate = selectedDate
        )
        return withContext(defaultDispatcher) {
            result.map { transactions ->
                transactions.toTransactionListContent(
                    displayCurrency = displayCurrency,
                    convert = exchangeRatesRepository::convert
                )
            }
        }
    }
}
