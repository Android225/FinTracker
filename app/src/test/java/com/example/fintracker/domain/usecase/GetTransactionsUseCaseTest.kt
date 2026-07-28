package com.example.fintracker.domain.usecase

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.AccountDraft
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.TransactionDraft
import com.example.fintracker.domain.TransactionType
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class GetTransactionsUseCaseTest {
    @Test
    fun `loads multiple accounts and sorts merged transactions newest first`() = runBlocking {
        val transactionsRepository = FakeTransactionsRepository(
            mapOf(
                1L to DataResult.Success(listOf(transaction(1, "2026-07-18T10:00:00Z", 1))),
                2L to DataResult.Success(listOf(transaction(2, "2026-07-19T10:00:00Z", 2)))
            )
        )
        val useCase = createUseCase(
            accountsResult = DataResult.Success(listOf(account(1), account(2))),
            transactionsRepository = transactionsRepository
        )

        val result = useCase(TransactionType.All, PERIOD_START, PERIOD_END)

        assertEquals(listOf(2L, 1L), (result as DataResult.Success).data.map(Transaction::id))
        assertEquals(setOf(1L, 2L), transactionsRepository.requestedAccountIds.toSet())
    }

    @Test
    fun `filters expense income and all transaction types`() = runBlocking {
        val transactionsRepository = FakeTransactionsRepository(
            mapOf(
                1L to DataResult.Success(
                    listOf(
                        transaction(1, "2026-07-18T10:00:00Z", 1, isIncome = false),
                        transaction(2, "2026-07-19T10:00:00Z", 1, isIncome = true)
                    )
                )
            )
        )
        val useCase = createUseCase(
            accountsResult = DataResult.Success(listOf(account(1))),
            transactionsRepository = transactionsRepository
        )

        val expenses = useCase(TransactionType.Expense, PERIOD_START, PERIOD_END)
        val incomes = useCase(TransactionType.Income, PERIOD_START, PERIOD_END)
        val all = useCase(TransactionType.All, PERIOD_START, PERIOD_END)

        assertEquals(listOf(1L), (expenses as DataResult.Success).data.map(Transaction::id))
        assertEquals(listOf(2L), (incomes as DataResult.Success).data.map(Transaction::id))
        assertEquals(listOf(2L, 1L), (all as DataResult.Success).data.map(Transaction::id))
    }

    @Test
    fun `uses explicit account ids without loading all accounts`() = runBlocking {
        val accountsRepository = FakeAccountsRepository(DataResult.Failure(AppError.Network))
        val transactionsRepository = FakeTransactionsRepository(
            mapOf(2L to DataResult.Success(listOf(transaction(2, "2026-07-19T10:00:00Z", 2))))
        )
        val useCase = GetTransactionsUseCase(
            accountsRepository,
            transactionsRepository,
            Dispatchers.Unconfined
        )

        val result = useCase(
            type = TransactionType.All,
            startDate = PERIOD_START,
            endDate = PERIOD_END,
            accountIds = setOf(2)
        )

        assertEquals(listOf(2L), (result as DataResult.Success).data.map(Transaction::id))
        assertEquals(0, accountsRepository.requestCount)
        assertEquals(listOf(2L), transactionsRepository.requestedAccountIds)
    }

    @Test
    fun `returns empty list when account has no transactions`() = runBlocking {
        val transactionsRepository = FakeTransactionsRepository(
            mapOf(1L to DataResult.Success(emptyList()))
        )
        val useCase = createUseCase(
            accountsResult = DataResult.Success(listOf(account(1))),
            transactionsRepository = transactionsRepository
        )

        val result = useCase(TransactionType.All, PERIOD_START, PERIOD_END)

        assertEquals(emptyList<Transaction>(), (result as DataResult.Success).data)
        assertEquals(listOf(1L), transactionsRepository.requestedAccountIds)
    }

    @Test
    fun `returns empty list without transaction requests when there are no accounts`() = runBlocking {
        val transactionsRepository = FakeTransactionsRepository(emptyMap())
        val useCase = createUseCase(
            accountsResult = DataResult.Success(emptyList()),
            transactionsRepository = transactionsRepository
        )

        val result = useCase(TransactionType.All, PERIOD_START, PERIOD_END)

        assertEquals(emptyList<Transaction>(), (result as DataResult.Success).data)
        assertEquals(emptyList<Long>(), transactionsRepository.requestedAccountIds)
    }

    @Test
    fun `returns failure when one account request fails`() = runBlocking {
        val useCase = createUseCase(
            accountsResult = DataResult.Success(listOf(account(1), account(2))),
            transactionsRepository = FakeTransactionsRepository(
                mapOf(
                    1L to DataResult.Success(listOf(transaction(1, "2026-07-18T10:00:00Z", 1))),
                    2L to DataResult.Failure(AppError.Network)
                )
            )
        )

        val result = useCase(TransactionType.All, PERIOD_START, PERIOD_END)

        assertEquals(AppError.Network, (result as DataResult.Failure).error)
    }

    @Test
    fun `rejects end date before start date without loading accounts`() = runBlocking {
        val accountsRepository = FakeAccountsRepository(DataResult.Success(listOf(account(1))))
        val useCase = GetTransactionsUseCase(
            accountsRepository,
            FakeTransactionsRepository(emptyMap()),
            Dispatchers.Unconfined
        )

        val result = useCase(TransactionType.All, PERIOD_END, PERIOD_START)

        assertEquals(AppError.BadRequest, (result as DataResult.Failure).error)
        assertEquals(0, accountsRepository.requestCount)
    }

    private fun createUseCase(
        accountsResult: DataResult<List<Account>>,
        transactionsRepository: TransactionsRepository
    ): GetTransactionsUseCase = GetTransactionsUseCase(
        accountsRepository = FakeAccountsRepository(accountsResult),
        transactionsRepository = transactionsRepository,
        defaultDispatcher = Dispatchers.Unconfined
    )

    private class FakeAccountsRepository(
        private val result: DataResult<List<Account>>
    ) : AccountsRepository {
        var requestCount = 0
            private set

        override suspend fun getAccounts(): DataResult<List<Account>> {
            requestCount++
            return result
        }

        override fun observeAccounts(): Flow<List<Account>> = flowOf(emptyList())

        override suspend fun getAccount(id: Long): DataResult<Account> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun saveAccount(id: Long?, draft: AccountDraft): DataResult<Long> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun deleteAccount(id: Long): DataResult<Unit> =
            DataResult.Failure(AppError.BadRequest)
    }

    private class FakeTransactionsRepository(
        private val results: Map<Long, DataResult<List<Transaction>>>
    ) : TransactionsRepository {
        val requestedAccountIds = mutableListOf<Long>()

        override suspend fun getTransactionsForPeriod(
            accountId: Long,
            startDate: LocalDate,
            endDate: LocalDate
        ): DataResult<List<Transaction>> {
            requestedAccountIds += accountId
            return results.getValue(accountId)
        }

        override suspend fun getTransaction(id: Long): DataResult<Transaction> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun saveTransaction(
            id: Long?,
            draft: TransactionDraft
        ): DataResult<Long> = DataResult.Failure(AppError.BadRequest)

        override suspend fun deleteTransaction(id: Long): DataResult<Unit> =
            DataResult.Failure(AppError.BadRequest)
    }

    private fun account(id: Long): Account = Account(
        id = id,
        serverId = id.toInt(),
        userId = 1,
        name = "Account $id",
        emoji = "💳",
        balance = BigDecimal.ZERO,
        currency = "RUB",
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )

    private fun transaction(
        id: Long,
        timestamp: String,
        accountId: Long,
        isIncome: Boolean = false
    ): Transaction {
        val instant = Instant.parse(timestamp)
        return Transaction(
            id = id,
            serverId = id.toInt(),
            account = AccountBrief(
                id = accountId,
                serverId = accountId.toInt(),
                name = "Account $accountId",
                emoji = "💳",
                balance = BigDecimal.ZERO,
                currency = "RUB"
            ),
            category = Category(id.toInt(), "Category $id", "", isIncome),
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
