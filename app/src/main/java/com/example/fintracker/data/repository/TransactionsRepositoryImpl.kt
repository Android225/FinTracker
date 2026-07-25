package com.example.fintracker.data.repository

import com.example.fintracker.data.local.dao.AccountDao
import com.example.fintracker.data.local.dao.CategoryDao
import com.example.fintracker.data.local.dao.TransactionDao
import com.example.fintracker.data.local.entity.SyncStatus
import com.example.fintracker.data.local.entity.TransactionEntity
import com.example.fintracker.data.local.mapper.toDomain
import com.example.fintracker.di.qualifier.IoDispatcher
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.TransactionDraft
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val clock: Clock,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TransactionsRepository {
    override suspend fun getTransactionsForPeriod(
        accountId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): DataResult<List<Transaction>> = withContext(ioDispatcher) {
        if (endDate.isBefore(startDate)) {
            return@withContext DataResult.Failure(AppError.BadRequest)
        }
        localResult {
            val zone = ZoneId.systemDefault()
            val start = startDate.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = endDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
            transactionDao.getForPeriod(accountId, start, end).map { it.toDomain() }
        }
    }

    override suspend fun getTransaction(id: Long): DataResult<Transaction> =
        withContext(ioDispatcher) {
            when (val result = localResult { transactionDao.getByLocalId(id)?.toDomain() }) {
                is DataResult.Success -> result.data
                    ?.let { DataResult.Success(it) }
                    ?: DataResult.Failure(AppError.NotFound)
                is DataResult.Failure -> result
            }
        }

    override suspend fun saveTransaction(
        id: Long?,
        draft: TransactionDraft
    ): DataResult<Long> = withContext(ioDispatcher) {
        localResult {
            require(draft.amount.signum() > 0) { "Amount must be positive" }
            requireNotNull(accountDao.getByLocalId(draft.accountId)) { "Account not found" }
            requireNotNull(categoryDao.getById(draft.categoryId)) { "Category not found" }

            val now = clock.millis()
            val current = id?.let { transactionDao.getByLocalId(it)?.transaction }
            if (id != null && current == null) error("Transaction not found")

            if (current == null) {
                transactionDao.insert(
                    TransactionEntity(
                        serverId = null,
                        accountLocalId = draft.accountId,
                        categoryId = draft.categoryId,
                        amount = draft.amount.toPlainString(),
                        transactionDateEpochMillis = draft.transactionDate.toEpochMilli(),
                        comment = draft.comment,
                        createdAtEpochMillis = now,
                        updatedAtEpochMillis = now,
                        syncStatus = SyncStatus.PENDING_CREATE
                    )
                )
            } else {
                transactionDao.update(
                    current.copy(
                        accountLocalId = draft.accountId,
                        categoryId = draft.categoryId,
                        amount = draft.amount.toPlainString(),
                        transactionDateEpochMillis = draft.transactionDate.toEpochMilli(),
                        comment = draft.comment,
                        updatedAtEpochMillis = now,
                        syncStatus = if (current.serverId == null) {
                            SyncStatus.PENDING_CREATE
                        } else {
                            SyncStatus.PENDING_UPDATE
                        }
                    )
                )
                current.localId
            }
        }
    }
}

private inline fun <T> localResult(block: () -> T): DataResult<T> = try {
    DataResult.Success(block())
} catch (exception: IllegalArgumentException) {
    DataResult.Failure(AppError.BadRequest)
} catch (exception: Exception) {
    DataResult.Failure(AppError.Unknown(exception.message))
}
