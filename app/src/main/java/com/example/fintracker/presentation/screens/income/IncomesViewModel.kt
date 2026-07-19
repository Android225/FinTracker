package com.example.fintracker.presentation.screens.income

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
import com.example.fintracker.presentation.screens.income.mapper.toIncomesContent
import com.example.fintracker.presentation.screens.income.model.IncomesContent
import com.example.fintracker.presentation.screens.income.model.IncomesEvent
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
    private val getTransactionsByType: GetTransactionsByTypeUseCase,
    clock: Clock,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    val selectedDate: LocalDate = LocalDate.now(clock)

    private val refreshPolicy = RefreshPolicy(clock)
    private val _uiState = MutableStateFlow(RefreshableUiState<IncomesContent>())
    val uiState: StateFlow<RefreshableUiState<IncomesContent>> = _uiState.asStateFlow()

    private val effectChannel = Channel<ScreenEffect>(Channel.BUFFERED)
    val effects: Flow<ScreenEffect> = effectChannel.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        loadIncomes(force = true)
    }

    fun onEvent(event: IncomesEvent) {
        when (event) {
            IncomesEvent.ScreenVisible -> loadIncomes(force = false)
            IncomesEvent.Refresh -> loadIncomes(force = true)
        }
    }

    // Загружает доходные транзакции за выбранный день с ограничением автообновлений.
    private fun loadIncomes(force: Boolean) {
        if (loadJob?.isActive == true || !refreshPolicy.shouldRefresh(force)) return
        loadJob = viewModelScope.launch {
            val previousState = _uiState.startRefreshing()
            val result = getTransactionsByType(
                startDate = selectedDate,
                endDate = selectedDate,
                isIncome = true
            )
            val uiResult = withContext(defaultDispatcher) {
                result.map { transactions -> transactions.toIncomesContent() }
            }
            if (uiResult is DataResult.Success) {
                refreshPolicy.markSuccessfulRefresh()
            }
            _uiState.updateFrom(
                result = uiResult,
                previousState = previousState,
                isEmpty = { it.incomes.isEmpty() },
                onRefreshError = effectChannel::send
            )
        }
    }
}
