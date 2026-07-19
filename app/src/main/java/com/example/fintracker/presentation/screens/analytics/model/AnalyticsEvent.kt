package com.example.fintracker.presentation.screens.analytics.model

import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind
import com.example.fintracker.domain.analytics.model.AnalyticsTransactionType

// Пользовательские события экрана аналитики.
sealed interface AnalyticsEvent {
    // Проверяет свежесть данных при возвращении на экран.
    data object ScreenVisible : AnalyticsEvent
    // Повторно загружает аналитику.
    data object Refresh : AnalyticsEvent
    // Возвращает фильтры к значениям по умолчанию.
    data object ResetFilters : AnalyticsEvent
    // Выбирает тип операций.
    data class SelectType(val type: AnalyticsTransactionType) : AnalyticsEvent
    // Выбирает готовый или произвольный период.
    data class SelectPeriod(
        val kind: AnalyticsPeriodKind,
        val period: AnalyticsPeriod
    ) : AnalyticsEvent

    // Выбирает категории операций.
    data class SelectCategories(val categoryIds: Set<Int>) : AnalyticsEvent
    // Выбирает конкретный счёт.
    data class SelectAccount(val accountId: Int?) : AnalyticsEvent
}
