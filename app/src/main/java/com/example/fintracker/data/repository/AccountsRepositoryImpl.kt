package com.example.fintracker.data.repository

import com.example.fintracker.data.local.dao.AccountDao
import com.example.fintracker.data.local.dao.TransactionDao
import com.example.fintracker.data.local.entity.AccountEntity
import com.example.fintracker.data.local.entity.SyncStatus
import com.example.fintracker.data.local.mapper.toDomain
import com.example.fintracker.di.qualifier.IoDispatcher
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountDraft
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Clock
import javax.inject.Inject

class AccountsRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val clock: Clock,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AccountsRepository {
    override suspend fun getAccounts(): DataResult<List<Account>> = withContext(ioDispatcher) {
        runLocal { accountDao.getAll().map(AccountEntity::toDomain) }
    }

    override fun observeAccounts(): Flow<List<Account>> =
        accountDao.observeAll().map { accounts -> accounts.map(AccountEntity::toDomain) }

    override suspend fun getAccount(id: Long): DataResult<Account> = withContext(ioDispatcher) {
        val account = runLocal { accountDao.getByLocalId(id)?.toDomain() }
        when (account) {
            is DataResult.Success -> account.data
                ?.let { DataResult.Success(it) }
                ?: DataResult.Failure(AppError.NotFound)
            is DataResult.Failure -> account
        }
    }

    override suspend fun saveAccount(
        id: Long?,
        draft: AccountDraft
    ): DataResult<Long> = withContext(ioDispatcher) {
        runLocal {
            val now = clock.millis()
            val current: AccountEntity? = id?.let { accountDao.getByLocalId(it) }
            if (id != null && current == null) error("Account not found")

            if (current == null) {
                accountDao.insert(
                    AccountEntity(
                        serverId = null,
                        userId = null,
                        name = draft.name,
                        emoji = draft.emoji,
                        balance = draft.balance.toPlainString(),
                        currency = draft.currency,
                        createdAtEpochMillis = now,
                        updatedAtEpochMillis = now,
                        syncStatus = SyncStatus.PENDING_CREATE
                    )
                )
            } else {
                accountDao.update(
                    current.copy(
                        name = draft.name,
                        emoji = draft.emoji,
                        balance = draft.balance.toPlainString(),
                        currency = draft.currency,
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

    override suspend fun deleteAccount(id: Long): DataResult<Unit> = withContext(ioDispatcher) {
        try {
            val account = accountDao.getByLocalId(id)
                ?: return@withContext DataResult.Failure(AppError.NotFound)
            if (transactionDao.countActiveForAccount(id) > 0) {
                return@withContext DataResult.Failure(AppError.Conflict)
            }
            if (account.serverId == null) {
                accountDao.deleteByLocalId(id)
            } else {
                accountDao.update(
                    account.copy(
                        updatedAtEpochMillis = clock.millis(),
                        syncStatus = SyncStatus.PENDING_DELETE
                    )
                )
            }
            DataResult.Success(Unit)
        } catch (exception: Exception) {
            DataResult.Failure(AppError.Unknown(exception.message))
        }
    }
}

private inline fun <T> runLocal(block: () -> T): DataResult<T> = try {
    DataResult.Success(block())
} catch (exception: Exception) {
    DataResult.Failure(AppError.Unknown(exception.message))
}
