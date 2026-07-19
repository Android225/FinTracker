package com.example.fintracker.domain.analytics.model

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category

data class AnalyticsData(
    val accounts: List<AccountBrief>,
    val categories: List<Category>,
    val calculation: AnalyticsCalculation
)
