package com.example.fintracker.domain.usecase

import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.time.LocalDate

class GetTransactionsForPeriodUseCase(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate,
        accountIds: Set<Int>? = null
    ): DataResult<List<Transaction>> {
        if (endDate.isBefore(startDate)) {
            return DataResult.Failure(AppError.BadRequest)
        }

        val resolvedAccountIds = accountIds ?: when (val accounts = accountsRepository.getAccounts()) {
            is DataResult.Failure -> return accounts
            is DataResult.Success -> accounts.data.mapTo(linkedSetOf()) { it.id }
        }

        if (resolvedAccountIds.isEmpty()) {
            return DataResult.Success(emptyList())
        }

        return withContext(defaultDispatcher) {
            val results = coroutineScope {
                resolvedAccountIds.map { accountId ->
                    async {
                        transactionsRepository.getTransactionsForPeriod(
                            accountId = accountId,
                            startDate = startDate,
                            endDate = endDate
                        )
                    }
                }.awaitAll()
            }

            val failure = results.firstOrNull { it is DataResult.Failure } as? DataResult.Failure
            if (failure != null) {
                failure
            } else {
                val transactions = results
                    .filterIsInstance<DataResult.Success<List<Transaction>>>()
                    .flatMap { it.data }
                    .sortedByDescending(Transaction::transactionDate)
                DataResult.Success(transactions)
            }
        }
    }
}
