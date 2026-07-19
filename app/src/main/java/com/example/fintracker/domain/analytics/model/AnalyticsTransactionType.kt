package com.example.fintracker.domain.analytics.model

enum class AnalyticsTransactionType(
    val isIncome: Boolean?
) {
    Expense(false),
    Income(true),
    All(null)
}
