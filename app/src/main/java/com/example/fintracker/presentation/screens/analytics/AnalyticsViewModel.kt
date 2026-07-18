package com.example.fintracker.presentation.screens.analytics

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.TransactionResponse
import com.example.fintracker.presentation.common.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            AnalyticsEvent.Refresh -> loadAnalytics()
            AnalyticsEvent.ResetFilters -> updateFilter(
                AnalyticsFilter(
                    type = AnalyticsTransactionType.Expense,
                    period = currentPeriod,
                    periodKind = AnalyticsPeriodKind.Month,
                    categoryIds = emptySet(),
                    accountId = null
                )
            )
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

    private fun updateFilter(updatedFilter: AnalyticsFilter) {
        filter = updatedFilter
        saveFilter(updatedFilter)
        loadAnalytics()
    }

    private fun loadAnalytics() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(MOCK_LOADING_DELAY_MS)
            _uiState.value = UiState.Content(buildContent())
        }
    }

    private fun buildContent(): AnalyticsContent {
        val transactions = analyticsMockTransactions
            .filter { filter.type.isIncome == null || it.category.isIncome == filter.type.isIncome }
            .filter { it.transactionDate.toLocalDate() in filter.period.start..filter.period.end }
            .filter { filter.categoryIds.isEmpty() || it.category.id in filter.categoryIds }
            .filter { filter.accountId == null || it.account.id == filter.accountId }
            .sortedByDescending { it.transactionDate.toLocalDateTime() }

        val categories = analyticsMockCategories.filter {
            filter.type.isIncome == null || it.isIncome == filter.type.isIncome
        }
        val totalAmount = transactions.sumOfAmount()

        return AnalyticsContent(
            totalAmount = totalAmount.formatMoney(),
            filter = filter.toUi(categories, analyticsMockAccounts),
            breakdown = transactions.toBreakdown(totalAmount),
            transactions = transactions.map(TransactionResponse::toUi),
            typeOptions = AnalyticsTransactionType.entries,
            periodOptions = periodOptions(),
            categoryOptions = categories.map { it.toOption(filter.categoryIds) },
            accountOptions = accountOptions(),
            selectedType = filter.type,
            selectedPeriod = filter.period
        )
    }

    private fun AnalyticsFilter.toUi(
        categories: List<Category>,
        accounts: List<AccountBrief>
    ): AnalyticsFilterUi {
        val selectedCategories = categories.filter { it.id in categoryIds }
        return AnalyticsFilterUi(
            type = type.label,
            period = period.format(),
            categories = if (selectedCategories.isEmpty()) {
                "Все статьи"
            } else {
                selectedCategories.joinToString { it.name }
            },
            account = accounts.firstOrNull { it.id == accountId }?.name ?: "Все счета"
        )
    }

    private fun periodOptions(): List<AnalyticsPeriodOption> {
        val today = LocalDate.now()
        val week = AnalyticsPeriod(today.minusDays(6), today)
        val month = AnalyticsPeriod(today.withDayOfMonth(1), today)
        val quarterStartMonth = ((today.monthValue - 1) / 3) * 3 + 1
        val quarter = AnalyticsPeriod(
            LocalDate.of(today.year, quarterStartMonth, 1),
            today
        )
        val year = AnalyticsPeriod(today.withDayOfYear(1), today)

        return listOf(
            AnalyticsPeriodOption(
                kind = AnalyticsPeriodKind.Custom,
                period = filter.period,
                title = "Произвольный",
                subtitle = filter.period.format(),
                selected = filter.periodKind == AnalyticsPeriodKind.Custom
            ),
            AnalyticsPeriodOption(
                AnalyticsPeriodKind.Week,
                week,
                "За неделю",
                selected = filter.periodKind == AnalyticsPeriodKind.Week
            ),
            AnalyticsPeriodOption(
                AnalyticsPeriodKind.Month,
                month,
                "За месяц",
                selected = filter.periodKind == AnalyticsPeriodKind.Month
            ),
            AnalyticsPeriodOption(
                AnalyticsPeriodKind.Quarter,
                quarter,
                "За квартал",
                selected = filter.periodKind == AnalyticsPeriodKind.Quarter
            ),
            AnalyticsPeriodOption(
                AnalyticsPeriodKind.Year,
                year,
                "За год",
                selected = filter.periodKind == AnalyticsPeriodKind.Year
            )
        )
    }

    private fun accountOptions(): List<AnalyticsAccountOption> {
        return listOf(
            AnalyticsAccountOption(
                id = null,
                title = "Все счета",
                emoji = "💳",
                selected = filter.accountId == null
            )
        ) + analyticsMockAccounts.map { account ->
            AnalyticsAccountOption(
                id = account.id,
                title = account.name,
                subtitle = if (account.name == "Сбербанк") "Дебетовая карта" else null,
                emoji = when (account.name) {
                    "Сбербанк" -> "🏦"
                    "Наличные" -> "💵"
                    else -> "💳"
                },
                selected = account.id == filter.accountId
            )
        }
    }

    private fun restoreFilter(): AnalyticsFilter {
        val defaultPeriod = currentPeriod
        val start = savedStateHandle.get<Long>(KEY_PERIOD_START)?.let(LocalDate::ofEpochDay)
        val end = savedStateHandle.get<Long>(KEY_PERIOD_END)?.let(LocalDate::ofEpochDay)
        val restoredPeriod = if (start != null && end != null && !end.isBefore(start)) {
            AnalyticsPeriod(start, end)
        } else {
            defaultPeriod
        }

        return AnalyticsFilter(
            type = savedStateHandle.get<String>(KEY_TYPE)
                ?.let { name -> AnalyticsTransactionType.entries.find { it.name == name } }
                ?: AnalyticsTransactionType.Expense,
            period = restoredPeriod,
            periodKind = savedStateHandle.get<String>(KEY_PERIOD_KIND)
                ?.let { name -> AnalyticsPeriodKind.entries.find { it.name == name } }
                ?: AnalyticsPeriodKind.Month,
            categoryIds = savedStateHandle.get<IntArray>(KEY_CATEGORY_IDS)?.toSet()
                ?: setOf(1, 2),
            accountId = savedStateHandle[KEY_ACCOUNT_ID]
        )
    }

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
    }
}

private val moneyFormat = DecimalFormat("#,###")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

private fun String.toLocalDate(): LocalDate = toLocalDateTime().toLocalDate()

private fun List<TransactionResponse>.sumOfAmount(): BigDecimal =
    fold(BigDecimal.ZERO) { sum, transaction -> sum + transaction.amount.toBigDecimal() }

private fun BigDecimal.formatMoney(): String =
    "${moneyFormat.format(this).replace(',', ' ')} ₽"

private fun AnalyticsPeriod.format(): String =
    "${start.format(dateFormatter)} – ${end.format(dateFormatter)}"

private fun List<TransactionResponse>.toBreakdown(
    totalAmount: BigDecimal
): List<AnalyticsBreakdownUi> {
    return groupBy { it.category }
        .map { (category, categoryTransactions) ->
            val amount = categoryTransactions.sumOfAmount()
            AnalyticsBreakdownUi(
                categoryId = category.id,
                title = category.name,
                amount = amount.formatMoney(),
                percent = if (totalAmount > BigDecimal.ZERO) {
                    amount.multiply(BigDecimal(100))
                        .divide(totalAmount, 0, RoundingMode.HALF_UP)
                        .toInt()
                } else {
                    0
                },
                color = category.color()
            )
        }
        .sortedByDescending { item ->
            item.amount.replace(" ₽", "").replace(" ", "").toBigDecimalOrNull()
                ?: BigDecimal.ZERO
        }
}

private fun Category.toOption(selectedIds: Set<Int>): AnalyticsCategoryOption =
    AnalyticsCategoryOption(
        id = id,
        title = name,
        emoji = emoji,
        selected = id in selectedIds
    )

private fun TransactionResponse.toUi(): AnalyticsTransactionUi =
    AnalyticsTransactionUi(
        id = id,
        emoji = category.emoji,
        title = comment ?: category.name,
        subtitle = account.name,
        amount = amount.toBigDecimal().formatMoney()
    )

private fun Category.color(): Color = when (id) {
    1, 9 -> Color(0xFFB69DF8)
    2, 10 -> Color(0xFF4FD8EB)
    else -> Color(0xFFF48FB1)
}
