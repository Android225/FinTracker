package com.example.fintracker.domain.analytics.model

import com.example.fintracker.domain.TransactionResponse
import java.math.BigDecimal

data class AnalyticsCalculation(
    val totalAmount: BigDecimal,
    val transactions: List<TransactionResponse>,
    val breakdown: List<AnalyticsCategoryBreakdown>
)

data class AnalyticsCategoryBreakdown(
    val categoryId: Int,
    val title: String,
    val amount: BigDecimal,
    val percent: Int
)
