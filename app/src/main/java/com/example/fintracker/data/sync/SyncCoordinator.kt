package com.example.fintracker.data.sync

import com.example.fintracker.data.local.dao.AccountDao
import com.example.fintracker.data.local.dao.CategoryDao
import com.example.fintracker.data.local.dao.TransactionDao
import com.example.fintracker.data.local.entity.AccountEntity
import com.example.fintracker.data.local.entity.CategoryEntity
import com.example.fintracker.data.local.entity.SyncStatus
import com.example.fintracker.data.local.entity.TransactionEntity
import com.example.fintracker.data.remote.api.FinanceApi
import com.example.fintracker.data.remote.dto.AccountMutationRequestDto
import com.example.fintracker.data.remote.dto.TransactionMutationRequestDto
import com.example.fintracker.data.remote.mapper.toDomain
import com.example.fintracker.data.remote.util.executeRemoteCall
import com.example.fintracker.di.qualifier.IoDispatcher
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncCoordinator @Inject constructor(
    private val api: FinanceApi,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val clock: Clock,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val mutex = Mutex()

    suspend fun sync(): DataResult<Unit> = withContext(ioDispatcher) {
        mutex.withLock {
            pushAccounts().onFailureReturn()
                ?: pullAccounts().onFailureReturn()
                ?: pullCategories().onFailureReturn()
                ?: pushTransactions().onFailureReturn()
                ?: pullTransactions().onFailureReturn()
                ?: DataResult.Success(Unit)
        }
    }

    private suspend fun pushAccounts(): DataResult<Unit> {
        for (account in accountDao.getPending()) {
            val request = AccountMutationRequestDto(
                name = account.name,
                emoji = account.emoji.ifBlank { null },
                balance = account.balance,
                currency = account.currency
            )
            val result = executeRemoteCall {
                if (account.serverId == null) {
                    api.createAccount(request)
                } else {
                    api.updateAccount(account.serverId, request)
                }.toDomain()
            }
            when (result) {
                is DataResult.Failure -> return result
                is DataResult.Success -> saveSyncedAccount(account.localId, result.data)
            }
        }
        return DataResult.Success(Unit)
    }

    private suspend fun pullAccounts(): DataResult<Unit> =
        executeRemoteCall { api.getAccounts().map { it.toDomain() } }
            .alsoSuccess { accounts ->
                accounts.forEach { remote ->
                    val existing = accountDao.getByServerId(requireNotNull(remote.serverId))
                    if (existing == null) {
                        accountDao.insert(remote.toEntity())
                    } else if (existing.syncStatus == SyncStatus.SYNCED) {
                        accountDao.update(remote.toEntity(localId = existing.localId))
                    }
                }
            }
            .unitResult()

    private suspend fun pullCategories(): DataResult<Unit> =
        executeRemoteCall { api.getCategories().map { it.toDomain() } }
            .alsoSuccess { categories ->
                categoryDao.upsertAll(categories.map(Category::toEntity))
            }
            .unitResult()

    private suspend fun pushTransactions(): DataResult<Unit> {
        for (transaction in transactionDao.getPending()) {
            val account = accountDao.getByLocalId(transaction.accountLocalId)
                ?: continue
            val accountServerId = account.serverId ?: continue
            val request = TransactionMutationRequestDto(
                accountId = accountServerId,
                categoryId = transaction.categoryId,
                amount = transaction.amount,
                transactionDate = Instant.ofEpochMilli(
                    transaction.transactionDateEpochMillis
                ).toString(),
                comment = transaction.comment
            )
            val result = executeRemoteCall {
                if (transaction.serverId == null) {
                    api.createTransaction(request)
                } else {
                    api.updateTransaction(transaction.serverId, request)
                }
            }
            when (result) {
                is DataResult.Failure -> return result
                is DataResult.Success -> {
                    val response = result.data
                    transactionDao.update(
                        transaction.copy(
                            serverId = requireNotNull(response.id),
                            amount = response.amount ?: transaction.amount,
                            transactionDateEpochMillis = response.transactionDate
                                ?.let(Instant::parse)
                                ?.toEpochMilli()
                                ?: transaction.transactionDateEpochMillis,
                            comment = response.comment,
                            createdAtEpochMillis = response.createdAt
                                ?.let(Instant::parse)
                                ?.toEpochMilli()
                                ?: transaction.createdAtEpochMillis,
                            updatedAtEpochMillis = response.updatedAt
                                ?.let(Instant::parse)
                                ?.toEpochMilli()
                                ?: clock.millis(),
                            syncStatus = SyncStatus.SYNCED
                        )
                    )
                }
            }
        }
        return DataResult.Success(Unit)
    }

    private suspend fun pullTransactions(): DataResult<Unit> {
        val remoteAccounts = accountDao.getAll().filter { it.serverId != null }
        for (account in remoteAccounts) {
            val result = executeRemoteCall {
                api.getTransactionsForPeriod(
                    accountId = requireNotNull(account.serverId),
                    startDate = SYNC_START_DATE.toString(),
                    endDate = LocalDate.now(clock).toString()
                ).map { it.toDomain() }
            }
            when (result) {
                is DataResult.Failure -> return result
                is DataResult.Success -> result.data.forEach { remote ->
                    saveRemoteTransaction(remote)
                }
            }
        }
        return DataResult.Success(Unit)
    }

    private suspend fun saveSyncedAccount(localId: Long, account: Account) {
        accountDao.markSynced(
            localId = localId,
            serverId = requireNotNull(account.serverId),
            userId = account.userId,
            name = account.name,
            emoji = account.emoji,
            balance = account.balance.toPlainString(),
            currency = account.currency,
            createdAt = account.createdAt.toEpochMilli(),
            updatedAt = account.updatedAt.toEpochMilli()
        )
    }

    private suspend fun saveRemoteTransaction(transaction: Transaction) {
        val remoteAccountId = requireNotNull(transaction.account.serverId)
        val account = accountDao.getByServerId(remoteAccountId) ?: return
        categoryDao.upsertAll(listOf(transaction.category.toEntity()))
        val remoteId = requireNotNull(transaction.serverId)
        val existing = transactionDao.getEntityByServerId(remoteId)
        if (existing?.syncStatus != null && existing.syncStatus != SyncStatus.SYNCED) return

        val entity = TransactionEntity(
            localId = existing?.localId ?: 0,
            serverId = remoteId,
            accountLocalId = account.localId,
            categoryId = transaction.category.id,
            amount = transaction.amount.toPlainString(),
            transactionDateEpochMillis = transaction.transactionDate.toEpochMilli(),
            comment = transaction.comment,
            createdAtEpochMillis = transaction.createdAt.toEpochMilli(),
            updatedAtEpochMillis = transaction.updatedAt.toEpochMilli(),
            syncStatus = SyncStatus.SYNCED
        )
        if (existing == null) transactionDao.insert(entity) else transactionDao.update(entity)
    }

    private companion object {
        val SYNC_START_DATE: LocalDate = LocalDate.of(1990, 1, 1)
    }
}

private fun Account.toEntity(localId: Long = 0): AccountEntity = AccountEntity(
    localId = localId,
    serverId = serverId,
    userId = userId,
    name = name,
    emoji = emoji,
    balance = balance.toPlainString(),
    currency = currency,
    createdAtEpochMillis = createdAt.toEpochMilli(),
    updatedAtEpochMillis = updatedAt.toEpochMilli(),
    syncStatus = SyncStatus.SYNCED
)

private fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    emoji = emoji,
    isIncome = isIncome
)

private fun DataResult<Unit>.onFailureReturn(): DataResult.Failure? =
    this as? DataResult.Failure

private suspend fun <T> DataResult<T>.alsoSuccess(
    block: suspend (T) -> Unit
): DataResult<T> {
    if (this is DataResult.Success) block(data)
    return this
}

private fun <T> DataResult<T>.unitResult(): DataResult<Unit> = when (this) {
    is DataResult.Success -> DataResult.Success(Unit)
    is DataResult.Failure -> this
}
