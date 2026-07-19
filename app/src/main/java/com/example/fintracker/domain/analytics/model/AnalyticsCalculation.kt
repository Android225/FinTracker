package com.example.fintracker.domain.analytics.model

import com.example.fintracker.domain.Transaction
import java.math.BigDecimal

// Результат расчёта аналитики по выбранным фильтрам.
data class AnalyticsCalculation(
    val totalAmount: BigDecimal,
    val transactions: List<Transaction>,
    val breakdown: List<AnalyticsCategoryBreakdown>
)

// Доля и сумма отдельной категории в аналитике.
data class AnalyticsCategoryBreakdown(
    val categoryId: Int,
    val title: String,
    val amount: BigDecimal,
    val percent: Int
)
