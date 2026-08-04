package com.example.fintracker.presentation.screens.analytics

import androidx.annotation.StringRes
import com.example.fintracker.R
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.analytics.model.AnalyticsPeriodKind

@StringRes
internal fun TransactionType.analyticsTitleResource(): Int = when (this) {
    TransactionType.Expense -> R.string.analytics_type_expense
    TransactionType.Income -> R.string.analytics_type_income
    TransactionType.All -> R.string.analytics_type_all
}

@StringRes
internal fun AnalyticsPeriodKind.analyticsTitleResource(): Int = when (this) {
    AnalyticsPeriodKind.Custom -> R.string.analytics_period_custom
    AnalyticsPeriodKind.Week -> R.string.analytics_period_week
    AnalyticsPeriodKind.Month -> R.string.analytics_period_month
    AnalyticsPeriodKind.Quarter -> R.string.analytics_period_quarter
    AnalyticsPeriodKind.Year -> R.string.analytics_period_year
}
