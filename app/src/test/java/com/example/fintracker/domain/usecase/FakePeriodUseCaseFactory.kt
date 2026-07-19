package com.example.fintracker.domain.usecase

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.Dispatchers
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

internal object FakePeriodUseCaseFactory {
    fun create(transactions: List<Transaction>): GetTransactionsForPeriodUseCase =
        GetTransactionsForPeriodUseCase(
            accountsRepository = object : AccountsRepository {
                override suspend fun getAccounts(): DataResult<List<Account>> =
                    DataResult.Success(
                        listOf(
                            Account(
                                id = ACCOUNT_ID,
                                userId = 1,
                                name = "Account",
                                emoji = "",
                                balance = BigDecimal.ZERO,
                                currency = "RUB",
                                createdAt = Instant.EPOCH,
                                updatedAt = Instant.EPOCH
                            )
                        )
                    )
            },
            transactionsRepository = object : TransactionsRepository {
                override suspend fun getTransactionsForPeriod(
                    accountId: Int,
                    startDate: LocalDate,
                    endDate: LocalDate
                ): DataResult<List<Transaction>> = DataResult.Success(transactions)
            },
            defaultDispatcher = Dispatchers.Unconfined
        )

    private const val ACCOUNT_ID = 1
}
