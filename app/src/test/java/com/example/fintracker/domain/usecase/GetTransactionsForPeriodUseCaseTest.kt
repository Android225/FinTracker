package com.example.fintracker.domain.usecase

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class GetTransactionsForPeriodUseCaseTest {
    @Test
    fun `loads every account concurrently and sorts merged transactions`() = runBlocking {
        val accountsRepository = FakeAccountsRepository(
            DataResult.Success(listOf(account(1), account(2)))
        )
        val transactionsRepository = FakeTransactionsRepository(
            mapOf(
                1 to DataResult.Success(listOf(transaction(1, "2026-07-18T10:00:00Z", 1))),
                2 to DataResult.Success(listOf(transaction(2, "2026-07-19T10:00:00Z", 2)))
            )
        )
        val useCase = GetTransactionsForPeriodUseCase(
            accountsRepository,
            transactionsRepository,
            Dispatchers.Unconfined
        )

        val result = useCase(PERIOD_START, PERIOD_END)

        assertEquals(listOf(2, 1), (result as DataResult.Success).data.map { it.id })
        assertEquals(setOf(1, 2), transactionsRepository.requestedAccountIds.toSet())
    }

    @Test
    fun `returns transaction failure without partial content`() = runBlocking {
        val useCase = GetTransactionsForPeriodUseCase(
            FakeAccountsRepository(DataResult.Success(listOf(account(1), account(2)))),
            FakeTransactionsRepository(
                mapOf(
                    1 to DataResult.Success(listOf(transaction(1, "2026-07-18T10:00:00Z", 1))),
                    2 to DataResult.Failure(AppError.Network)
                )
            ),
            Dispatchers.Unconfined
        )

        val result = useCase(PERIOD_START, PERIOD_END)

        assertEquals(AppError.Network, (result as DataResult.Failure).error)
    }

    private class FakeAccountsRepository(
        private val result: DataResult<List<Account>>
    ) : AccountsRepository {
        override suspend fun getAccounts(): DataResult<List<Account>> = result
    }

    private class FakeTransactionsRepository(
        private val results: Map<Int, DataResult<List<Transaction>>>
    ) : TransactionsRepository {
        val requestedAccountIds = mutableListOf<Int>()

        override suspend fun getTransactionsForPeriod(
            accountId: Int,
            startDate: LocalDate,
            endDate: LocalDate
        ): DataResult<List<Transaction>> {
            requestedAccountIds += accountId
            return results.getValue(accountId)
        }
    }

    private fun account(id: Int): Account = Account(
        id = id,
        userId = 1,
        name = "Account $id",
        emoji = "💳",
        balance = BigDecimal.ZERO,
        currency = "RUB",
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )

    private fun transaction(id: Int, timestamp: String, accountId: Int): Transaction {
        val instant = Instant.parse(timestamp)
        return Transaction(
            id = id,
            account = AccountBrief(
                accountId,
                "Account $accountId",
                "💳",
                BigDecimal.ZERO,
                "RUB"
            ),
            category = Category(1, "Category", "🛒", false),
            amount = BigDecimal.ONE,
            transactionDate = instant,
            comment = null,
            createdAt = instant,
            updatedAt = instant
        )
    }

    private companion object {
        val PERIOD_START: LocalDate = LocalDate.of(2026, 7, 1)
        val PERIOD_END: LocalDate = LocalDate.of(2026, 7, 19)
    }
}
