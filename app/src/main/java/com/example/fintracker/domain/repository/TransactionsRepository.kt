package com.example.fintracker.domain.repository

import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.TransactionDraft
import com.example.fintracker.domain.result.DataResult
import java.time.LocalDate

interface TransactionsRepository {
    suspend fun getTransactionsForPeriod(
        accountId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): DataResult<List<Transaction>>

    suspend fun getTransaction(id: Long): DataResult<Transaction>
    suspend fun saveTransaction(id: Long?, draft: TransactionDraft): DataResult<Long>
    suspend fun deleteTransaction(id: Long): DataResult<Unit>
}
