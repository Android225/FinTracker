package com.example.fintracker.presentation.screens.analytics.model

import com.example.fintracker.domain.analytics.model.AnalyticsPeriod
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind

data class AnalyticsPeriodOption(
    val kind: AnalyticsPeriodKind,
    val period: AnalyticsPeriod,
    val title: String,
    val subtitle: String? = null,
    val selected: Boolean
)

data class AnalyticsCategoryOption(
    val id: Int,
    val title: String,
    val emoji: String,
    val selected: Boolean
)

data class AnalyticsAccountOption(
    val id: Int?,
    val title: String,
    val subtitle: String? = null,
    val emoji: String,
    val selected: Boolean
)
