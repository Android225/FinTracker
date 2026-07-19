package com.example.fintracker.presentation.screens.analytics.model

import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind

// Вариант периода для фильтра аналитики.
data class AnalyticsPeriodOption(
    val kind: AnalyticsPeriodKind,
    val period: AnalyticsPeriod,
    val title: String,
    val subtitle: String? = null,
    val selected: Boolean
)

// Вариант категории для фильтра аналитики.
data class AnalyticsCategoryOption(
    val id: Int,
    val title: String,
    val emoji: String,
    val selected: Boolean
)

// Вариант счёта для фильтра аналитики.
data class AnalyticsAccountOption(
    val id: Int?,
    val title: String,
    val subtitle: String? = null,
    val emoji: String,
    val selected: Boolean
)
