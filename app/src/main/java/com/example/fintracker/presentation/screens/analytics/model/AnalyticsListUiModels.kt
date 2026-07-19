package com.example.fintracker.presentation.screens.analytics.model

// Отображаемая разбивка суммы по категории.
data class AnalyticsBreakdownUi(
    val categoryId: Int,
    val title: String,
    val amount: String,
    val percent: Int
)

// Отображаемая операция в списке аналитики.
data class AnalyticsTransactionUi(
    val id: Int,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val amount: String
)
