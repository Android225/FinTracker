package com.example.fintracker.domain.analytics.model

import com.example.fintracker.domain.Transaction
import java.math.BigDecimal

data class AnalyticsCalculation(
    val totalAmount: BigDecimal,
    val transactions: List<Transaction>,
    val breakdown: List<AnalyticsCategoryBreakdown>
)

data class AnalyticsCategoryBreakdown(
    val categoryId: Int,
    val title: String,
    val amount: BigDecimal,
    val percent: Int
)
