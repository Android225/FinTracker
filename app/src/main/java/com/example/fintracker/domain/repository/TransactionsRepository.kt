package com.example.fintracker.domain.repository

import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.result.DataResult
import java.time.LocalDate

interface TransactionsRepository {
    suspend fun getTransactionsForPeriod(
        accountId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): DataResult<List<Transaction>>
}
