package com.example.fintracker.presentation.screens.analytics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.di.qualifier.DefaultDispatcher
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.domain.result.map
import com.example.fintracker.domain.usecase.GetAnalyticsUseCase
import com.example.fintracker.presentation.common.model.ScreenEffect
import com.example.fintracker.presentation.common.model.RefreshableUiState
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.common.model.startRefreshing
import com.example.fintracker.presentation.common.model.updateFrom
import com.example.fintracker.presentation.common.refresh.RefreshPolicy
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
class AnalyticsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAnalytics: GetAnalyticsUseCase,
    private val clock: Clock,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val refreshPolicy = RefreshPolicy(clock)
    private val _uiState = MutableStateFlow(RefreshableUiState<AnalyticsContent>())
    val uiState: StateFlow<RefreshableUiState<AnalyticsContent>> = _uiState.asStateFlow()

    private val effectChannel = Channel<ScreenEffect>(Channel.BUFFERED)
    val effects: Flow<ScreenEffect> = effectChannel.receiveAsFlow()

    private var filter = restoreFilter()
    private var loadJob: Job? = null

    init {
        loadAnalytics(force = true)
    }

    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            AnalyticsEvent.ScreenVisible -> loadAnalytics(force = false)
            AnalyticsEvent.Refresh -> loadAnalytics(force = true)
            is AnalyticsEvent.SelectType -> updateFilter(
                filter.copy(type = event.type, categoryIds = emptySet())
            )
            is AnalyticsEvent.SelectPeriod -> updateFilter(
                filter.copy(period = event.period, periodKind = event.kind)
            )
            is AnalyticsEvent.SelectCategories -> updateFilter(
                filter.copy(categoryIds = event.categoryIds)
            )
            is AnalyticsEvent.SelectAccount -> updateFilter(
                filter.copy(accountId = event.accountId)
            )
        }
    }

    // Сохраняет новый фильтр и заменяет незавершённый расчёт актуальным запросом.
    private fun updateFilter(updatedFilter: AnalyticsFilter) {
        filter = updatedFilter
        saveFilter(updatedFilter)
        loadAnalytics(force = true, replaceRunning = true)
    }

    // Загружает и пересчитывает аналитику, заменяя старый запрос при смене фильтра.
    private fun loadAnalytics(
        force: Boolean,
        replaceRunning: Boolean = false
    ) {
        if (loadJob?.isActive == true) {
            if (!replaceRunning) return
            loadJob?.cancel()
        }
        if (!refreshPolicy.shouldRefresh(force)) return

        loadJob = viewModelScope.launch {
            val previousState = _uiState.startRefreshing()
            val loadedFilter = filter
            val today = LocalDate.now(clock)
            val result = getAnalytics(
                filter = loadedFilter,
                zoneId = clock.zone
            )
            val uiResult = withContext(defaultDispatcher) {
                result.map { data ->
                    data.calculation.toUiContent(
                        filter = loadedFilter,
                        categories = data.categories,
                        accounts = data.accounts,
                        today = today
                    )
                }
            }
            if (uiResult is DataResult.Success) {
                refreshPolicy.markSuccessfulRefresh()
            }
            _uiState.updateFrom(
                result = uiResult,
                previousState = previousState,
                isEmpty = { content -> content.transactions.isEmpty() },
                onRefreshError = effectChannel::send
            )
        }
    }

    // Восстанавливает фильтры после пересоздания экрана и отбрасывает некорректный период.
    private fun restoreFilter(): AnalyticsFilter {
        val defaultFilter = defaultFilter()
        val start = savedStateHandle.get<Long>(KEY_PERIOD_START)?.let(LocalDate::ofEpochDay)
        val end = savedStateHandle.get<Long>(KEY_PERIOD_END)?.let(LocalDate::ofEpochDay)
        val restoredPeriod = if (start != null && end != null && !end.isBefore(start)) {
            AnalyticsPeriod(start, end)
        } else {
            defaultFilter.period
        }

        return AnalyticsFilter(
            type = savedStateHandle.get<String>(KEY_TYPE)
                ?.let { savedName ->
                    TransactionType.entries.find { it.name == savedName }
                }
                ?: defaultFilter.type,
            period = restoredPeriod,
            periodKind = savedStateHandle.get<String>(KEY_PERIOD_KIND)
                ?.let { savedName -> AnalyticsPeriodKind.entries.find { it.name == savedName } }
                ?: defaultFilter.periodKind,
            categoryIds = savedStateHandle.get<IntArray>(KEY_CATEGORY_IDS)?.toSet().orEmpty(),
            accountId = savedStateHandle[KEY_ACCOUNT_ID]
        )
    }

    // Формирует период от первого дня текущего месяца до сегодняшней даты.
    private fun defaultFilter(): AnalyticsFilter {
        val today = LocalDate.now(clock)
        return AnalyticsFilter(
            type = TransactionType.Expense,
            period = AnalyticsPeriod(today.withDayOfMonth(1), today),
            periodKind = AnalyticsPeriodKind.Month,
            categoryIds = emptySet(),
            accountId = null
        )
    }

    // Сохраняет только примитивные значения, поддерживаемые SavedStateHandle.
    private fun saveFilter(filter: AnalyticsFilter) {
        savedStateHandle[KEY_TYPE] = filter.type.name
        savedStateHandle[KEY_PERIOD_START] = filter.period.start.toEpochDay()
        savedStateHandle[KEY_PERIOD_END] = filter.period.end.toEpochDay()
        savedStateHandle[KEY_PERIOD_KIND] = filter.periodKind.name
        savedStateHandle[KEY_CATEGORY_IDS] = filter.categoryIds.toIntArray()
        savedStateHandle[KEY_ACCOUNT_ID] = filter.accountId
    }

    private companion object {
        const val KEY_TYPE = "analytics_type"
        const val KEY_PERIOD_START = "analytics_period_start"
        const val KEY_PERIOD_END = "analytics_period_end"
        const val KEY_PERIOD_KIND = "analytics_period_kind"
        const val KEY_CATEGORY_IDS = "analytics_category_ids"
        const val KEY_ACCOUNT_ID = "analytics_account_id"
    }
}
