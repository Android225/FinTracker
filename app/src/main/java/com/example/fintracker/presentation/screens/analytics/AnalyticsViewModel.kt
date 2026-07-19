package com.example.fintracker.presentation.screens.analytics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.data.mock.analytics.analyticsMockAccounts
import com.example.fintracker.data.mock.analytics.analyticsMockCategories
import com.example.fintracker.data.mock.analytics.analyticsMockTransactions
import com.example.fintracker.domain.analytics.AnalyticsCalculator
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.analytics.mapper.toUiContent
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsContent
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

// Управляет фильтрами, загрузкой и состоянием аналитики.
class AnalyticsViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AnalyticsContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<AnalyticsContent>> = _uiState.asStateFlow()

    private val currentPeriod: AnalyticsPeriod
        get() {
            val today = LocalDate.now()
            return AnalyticsPeriod(today.withDayOfMonth(1), today)
        }

    private var filter = restoreFilter()
    private var loadJob: Job? = null

    init {
        loadAnalytics()
    }

    // Обрабатывает действия пользователя на экране аналитики.
    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            AnalyticsEvent.Refresh -> loadAnalytics()
            AnalyticsEvent.ResetFilters -> updateFilter(defaultFilter())
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

    // Сохраняет новый фильтр и пересчитывает аналитику.
    private fun updateFilter(updatedFilter: AnalyticsFilter) {
        filter = updatedFilter
        saveFilter(updatedFilter)
        loadAnalytics()
    }

    // Выполняет mock-загрузку и публикует рассчитанные данные.
    private fun loadAnalytics() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(MOCK_LOADING_DELAY_MS)

            val calculation = AnalyticsCalculator.calculate(
                transactions = analyticsMockTransactions,
                filter = filter
            )
            _uiState.value = UiState.Content(
                calculation.toUiContent(
                    filter = filter,
                    categories = analyticsMockCategories,
                    accounts = analyticsMockAccounts,
                    today = LocalDate.now()
                )
            )
        }
    }

    // Восстанавливает фильтр после пересоздания экрана.
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
                    AnalyticsTransactionType.entries.find { it.name == savedName }
                }
                ?: defaultFilter.type,
            period = restoredPeriod,
            periodKind = savedStateHandle.get<String>(KEY_PERIOD_KIND)
                ?.let { savedName -> AnalyticsPeriodKind.entries.find { it.name == savedName } }
                ?: defaultFilter.periodKind,
            categoryIds = savedStateHandle.get<IntArray>(KEY_CATEGORY_IDS)?.toSet()
                ?: DEFAULT_CATEGORY_IDS,
            accountId = savedStateHandle[KEY_ACCOUNT_ID]
        )
    }

    // Создаёт фильтр аналитики по умолчанию.
    private fun defaultFilter(): AnalyticsFilter = AnalyticsFilter(
        type = AnalyticsTransactionType.Expense,
        period = currentPeriod,
        periodKind = AnalyticsPeriodKind.Month,
        categoryIds = emptySet(),
        accountId = null
    )

    // Сохраняет фильтр в SavedStateHandle.
    private fun saveFilter(filter: AnalyticsFilter) {
        savedStateHandle[KEY_TYPE] = filter.type.name
        savedStateHandle[KEY_PERIOD_START] = filter.period.start.toEpochDay()
        savedStateHandle[KEY_PERIOD_END] = filter.period.end.toEpochDay()
        savedStateHandle[KEY_PERIOD_KIND] = filter.periodKind.name
        savedStateHandle[KEY_CATEGORY_IDS] = filter.categoryIds.toIntArray()
        savedStateHandle[KEY_ACCOUNT_ID] = filter.accountId
    }

    private companion object {
        const val MOCK_LOADING_DELAY_MS = 150L
        const val KEY_TYPE = "analytics_type"
        const val KEY_PERIOD_START = "analytics_period_start"
        const val KEY_PERIOD_END = "analytics_period_end"
        const val KEY_PERIOD_KIND = "analytics_period_kind"
        const val KEY_CATEGORY_IDS = "analytics_category_ids"
        const val KEY_ACCOUNT_ID = "analytics_account_id"
        val DEFAULT_CATEGORY_IDS = setOf(1, 2)
    }
}
