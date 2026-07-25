package com.example.fintracker.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fintracker.data.local.entity.AccountEntity
import com.example.fintracker.data.local.entity.CategoryEntity
import com.example.fintracker.data.local.entity.SyncStatus
import com.example.fintracker.data.local.entity.TransactionEntity
import com.example.fintracker.data.repository.AccountsRepositoryImpl
import com.example.fintracker.data.repository.TransactionsRepositoryImpl
import com.example.fintracker.domain.AccountDraft
import com.example.fintracker.domain.TransactionDraft
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@RunWith(AndroidJUnit4::class)
class FinTrackerDatabaseTest {
    private lateinit var database: FinTrackerDatabase

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FinTrackerDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun offlineTransactionIsStoredWithItsAccountAndCategory() = runBlocking {
        val accountId = database.accountDao().insert(
            AccountEntity(
                serverId = null,
                userId = null,
                name = "Offline",
                emoji = "💳",
                balance = "100",
                currency = "RUB",
                createdAtEpochMillis = 1,
                updatedAtEpochMillis = 1,
                syncStatus = SyncStatus.PENDING_CREATE
            )
        )
        database.categoryDao().upsertAll(
            listOf(CategoryEntity(id = 7, name = "Food", emoji = "🍔", isIncome = false))
        )
        database.transactionDao().insert(
            TransactionEntity(
                serverId = null,
                accountLocalId = accountId,
                categoryId = 7,
                amount = "42.50",
                transactionDateEpochMillis = 10,
                comment = "Offline purchase",
                createdAtEpochMillis = 10,
                updatedAtEpochMillis = 10,
                syncStatus = SyncStatus.PENDING_CREATE
            )
        )

        val stored = database.transactionDao().getForPeriod(accountId, 0, 20).single()

        assertEquals("Offline", stored.account.name)
        assertEquals("Food", stored.category.name)
        assertEquals(SyncStatus.PENDING_CREATE, stored.transaction.syncStatus)
    }

    @Test
    fun repositoriesCreateAccountAndTransactionWithoutNetwork() = runBlocking {
        val clock = Clock.fixed(Instant.parse("2026-07-25T10:00:00Z"), ZoneOffset.UTC)
        val accountsRepository = AccountsRepositoryImpl(
            database.accountDao(),
            clock,
            Dispatchers.Unconfined
        )
        val transactionsRepository = TransactionsRepositoryImpl(
            database.transactionDao(),
            database.accountDao(),
            database.categoryDao(),
            clock,
            Dispatchers.Unconfined
        )
        database.categoryDao().upsertAll(
            listOf(CategoryEntity(id = 7, name = "Food", emoji = "🍔", isIncome = false))
        )

        val accountId = (accountsRepository.saveAccount(
            id = null,
            draft = AccountDraft("Offline", "💳", BigDecimal("100"), "RUB")
        ) as DataResult.Success).data
        val transactionId = transactionsRepository.saveTransaction(
            id = null,
            draft = TransactionDraft(
                accountId = accountId,
                categoryId = 7,
                amount = BigDecimal("42.50"),
                transactionDate = clock.instant(),
                comment = null
            )
        )

        assert(transactionId is DataResult.Success)
        assertEquals(SyncStatus.PENDING_CREATE, database.accountDao().getPending().single().syncStatus)
        assertEquals(
            SyncStatus.PENDING_CREATE,
            database.transactionDao().getPending().single().syncStatus
        )
    }
}
