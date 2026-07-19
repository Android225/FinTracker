package com.example.fintracker.data.repository

import com.example.fintracker.data.remote.api.FinanceApi
import com.example.fintracker.data.remote.mapper.toDomain
import com.example.fintracker.data.remote.util.executeRemoteCall
import com.example.fintracker.di.qualifier.IoDispatcher
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val api: FinanceApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TransactionsRepository {
    override suspend fun getTransactionsForPeriod(
        accountId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): DataResult<List<Transaction>> = withContext(ioDispatcher) {
        if (endDate.isBefore(startDate)) {
            return@withContext DataResult.Failure(AppError.BadRequest)
        }
        executeRemoteCall {
            api.getTransactionsForPeriod(
                accountId = accountId,
                startDate = startDate.toString(),
                endDate = endDate.toString()
            ).map { it.toDomain() }
        }
    }
}
