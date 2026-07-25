package com.example.fintracker.presentation.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.di.qualifier.DefaultDispatcher
import com.example.fintracker.data.sync.SyncCoordinator
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.domain.result.map
import com.example.fintracker.presentation.common.model.ScreenUiState
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.common.model.startRefreshing
import com.example.fintracker.presentation.common.model.updateFrom
import com.example.fintracker.presentation.common.refresh.RefreshPolicy
import com.example.fintracker.presentation.screens.accounts.mapper.toAccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsEvent
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
class AccountsViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val syncCoordinator: SyncCoordinator? = null,
    clock: Clock,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    val selectedDate: LocalDate = LocalDate.now(clock)

    private val _uiState = MutableStateFlow(ScreenUiState<AccountsContent>())
    val uiState: StateFlow<ScreenUiState<AccountsContent>> = _uiState.asStateFlow()

    private val errorChannel = Channel<AppError>(Channel.BUFFERED)
    val errors: Flow<AppError> = errorChannel.receiveAsFlow()

    private val refreshPolicy = RefreshPolicy(clock)
    private var loadJob: Job? = null

    init {
        loadAccounts(force = true)
    }

    fun onEvent(event: AccountsEvent) {
        when (event) {
            AccountsEvent.ScreenVisible -> loadAccounts(force = false)
            AccountsEvent.Refresh -> loadAccounts(force = true)
        }
    }

    // Загружает счета, если данные устарели или обновление было запрошено явно.
    private fun loadAccounts(force: Boolean) {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            val previousState = _uiState.startRefreshing()
            val shouldSync = refreshPolicy.shouldRefresh(force)
            val syncResult = if (shouldSync) {
                syncCoordinator?.sync() ?: DataResult.Success(Unit)
            } else {
                DataResult.Success(Unit)
            }
            val result = accountsRepository.getAccounts()
            val uiResult = withContext(defaultDispatcher) {
                result.map { accounts -> accounts.toAccountsContent() }
            }
            if (shouldSync && syncResult is DataResult.Success) {
                refreshPolicy.markSuccessfulRefresh()
            }
            val effectiveResult = when {
                syncResult is DataResult.Failure &&
                    uiResult is DataResult.Success &&
                    uiResult.data.accounts.isEmpty() -> syncResult
                else -> uiResult
            }
            if (syncResult is DataResult.Failure && uiResult is DataResult.Success &&
                uiResult.data.accounts.isNotEmpty()
            ) {
                errorChannel.send(syncResult.error)
            }
            _uiState.updateFrom(
                result = effectiveResult,
                previousState = previousState,
                isEmpty = { it.accounts.isEmpty() },
                onRefreshError = errorChannel::send
            )
        }
    }
}
