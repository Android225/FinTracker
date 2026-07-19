package com.example.fintracker.domain.analytics.model

// Тип операций, отображаемых в аналитике.
enum class AnalyticsTransactionType(
    val isIncome: Boolean?
) {
    Expense(false),
    Income(true),
    All(null)
}
