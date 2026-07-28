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
import com.example.fintracker.data.remote.dto.TransactionMutationResponseDto
import com.example.fintracker.data.remote.mapper.toDomain
import com.example.fintracker.data.remote.util.executeRemoteCall
import com.example.fintracker.data.remote.util.ServerRetryPolicy
import com.example.fintracker.di.qualifier.IoDispatcher
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
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
            pushAccountUpserts().onFailureReturn()
                ?: pullAccounts().onFailureReturn()
                ?: pullCategories().onFailureReturn()
                ?: pushTransactions().onFailureReturn()
                ?: pushAccountDeletes().onFailureReturn()
                ?: pullAccounts().onFailureReturn()
                ?: pullTransactions().onFailureReturn()
                ?: DataResult.Success(Unit)
        }
    }

    private suspend fun pushAccountUpserts(): DataResult<Unit> {
        for (account in accountDao.getPending().filterNot {
            it.syncStatus == SyncStatus.PENDING_DELETE
        }) {
            val request = AccountMutationRequestDto(
                name = account.name,
                emoji = account.emoji.ifBlank { null },
                balance = account.balance,
                currency = account.currency
            )
            val result = if (account.serverId == null) {
                createAccountSafely(account, request)
            } else {
                executeRemoteCall {
                    api.updateAccount(account.serverId, request).toDomain()
                }
            }
            when (result) {
                is DataResult.Failure -> return result
                is DataResult.Success -> saveSyncedAccount(account, result.data)
            }
        }
        return DataResult.Success(Unit)
    }

    private suspend fun pushAccountDeletes(): DataResult<Unit> {
        for (account in accountDao.getPending().filter {
            it.syncStatus == SyncStatus.PENDING_DELETE
        }) {
            val serverId = account.serverId
            if (serverId == null) {
                accountDao.deleteByLocalId(account.localId)
                continue
            }
            when (val result = executeRemoteCall { api.deleteAccount(serverId) }) {
                is DataResult.Success -> accountDao.deleteByLocalId(account.localId)
                is DataResult.Failure -> when (result.error) {
                    AppError.NotFound -> accountDao.deleteByLocalId(account.localId)
                    AppError.Conflict -> {
                        accountDao.update(account.copy(syncStatus = SyncStatus.SYNCED))
                        return result
                    }
                    else -> return result
                }
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
            if (transaction.syncStatus == SyncStatus.PENDING_DELETE) {
                val serverId = transaction.serverId
                if (serverId == null) {
                    transactionDao.deleteByLocalId(transaction.localId)
                    continue
                }
                when (val result = executeRemoteCall { api.deleteTransaction(serverId) }) {
                    is DataResult.Success -> transactionDao.deleteByLocalId(transaction.localId)
                    is DataResult.Failure -> {
                        if (result.error == AppError.NotFound) {
                            transactionDao.deleteByLocalId(transaction.localId)
                        } else {
                            return result
                        }
                    }
                }
                continue
            }
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
            val result = if (transaction.serverId == null) {
                createTransactionSafely(transaction, accountServerId, request)
            } else {
                executeRemoteCall {
                    api.updateTransaction(transaction.serverId, request)
                }.alsoSuccess { response ->
                    saveSyncedTransaction(transaction, response)
                }.unitResult()
            }
            if (result is DataResult.Failure) {
                return result
            }
        }
        return DataResult.Success(Unit)
    }

    private suspend fun createAccountSafely(
        localAccount: AccountEntity,
        request: AccountMutationRequestDto
    ): DataResult<Account> {
        when (val existing = findMatchingRemoteAccount(localAccount)) {
            is DataResult.Failure -> return existing
            is DataResult.Success -> existing.data?.let { return DataResult.Success(it) }
        }

        repeat(ServerRetryPolicy.MAX_RETRIES + 1) { attempt ->
            val createResult = executeRemoteCall { api.createAccount(request).toDomain() }
            when (createResult) {
                is DataResult.Success -> return createResult
                is DataResult.Failure -> {
                    if (createResult.error != AppError.Server) {
                        return createResult
                    }
                    when (val existing = findMatchingRemoteAccount(localAccount)) {
                        is DataResult.Failure -> return existing
                        is DataResult.Success -> {
                            existing.data?.let { return DataResult.Success(it) }
                        }
                    }
                    if (attempt < ServerRetryPolicy.MAX_RETRIES) {
                        delay(ServerRetryPolicy.DELAY_MILLIS)
                    }
                }
            }
        }
        return DataResult.Failure(AppError.Server)
    }

    private suspend fun findMatchingRemoteAccount(
        local: AccountEntity
    ): DataResult<Account?> =
        when (val result = executeRemoteCall { api.getAccounts().map { it.toDomain() } }) {
            is DataResult.Failure -> result
            is DataResult.Success -> DataResult.Success(
                result.data.firstOrNull { remote ->
                    remote.name == local.name &&
                        remote.emoji == local.emoji &&
                        remote.currency == local.currency &&
                        remote.balance.compareTo(local.balance.toBigDecimal()) == 0 &&
                        remote.createdAt.toEpochMilli() >=
                        local.createdAtEpochMillis - ACCOUNT_CLOCK_SKEW_MILLIS
                }
            )
        }

    private suspend fun createTransactionSafely(
        localTransaction: TransactionEntity,
        accountServerId: Int,
        request: TransactionMutationRequestDto
    ): DataResult<Unit> {
        when (val existing = findMatchingRemoteTransaction(localTransaction, accountServerId)) {
            is DataResult.Failure -> return existing
            is DataResult.Success -> existing.data?.let { remote ->
                saveSyncedTransaction(localTransaction, remote)
                return DataResult.Success(Unit)
            }
        }

        repeat(ServerRetryPolicy.MAX_RETRIES + 1) { attempt ->
            val createResult = executeRemoteCall { api.createTransaction(request) }
            when (createResult) {
                is DataResult.Success -> {
                    saveSyncedTransaction(localTransaction, createResult.data)
                    return DataResult.Success(Unit)
                }
                is DataResult.Failure -> {
                    if (createResult.error != AppError.Server) {
                        return createResult
                    }
                    when (
                        val existing = findMatchingRemoteTransaction(
                            localTransaction,
                            accountServerId
                        )
                    ) {
                        is DataResult.Failure -> return existing
                        is DataResult.Success -> existing.data?.let { remote ->
                            saveSyncedTransaction(localTransaction, remote)
                            return DataResult.Success(Unit)
                        }
                    }
                    if (attempt < ServerRetryPolicy.MAX_RETRIES) {
                        delay(ServerRetryPolicy.DELAY_MILLIS)
                    }
                }
            }
        }
        return DataResult.Failure(AppError.Server)
    }

    private suspend fun findMatchingRemoteTransaction(
        local: TransactionEntity,
        accountServerId: Int
    ): DataResult<Transaction?> {
        val transactionDate = Instant.ofEpochMilli(local.transactionDateEpochMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        return when (
            val result = executeRemoteCall {
                api.getTransactionsForPeriod(
                    accountId = accountServerId,
                    startDate = transactionDate.minusDays(1).toString(),
                    endDate = transactionDate.plusDays(1).toString()
                ).map { it.toDomain() }
            }
        ) {
            is DataResult.Failure -> result
            is DataResult.Success -> DataResult.Success(
                result.data.firstOrNull { remote ->
                    remote.category.id == local.categoryId &&
                        remote.amount.compareTo(local.amount.toBigDecimal()) == 0 &&
                        remote.transactionDate.toEpochMilli() ==
                        local.transactionDateEpochMillis &&
                        remote.comment.orEmpty() == local.comment.orEmpty()
                }
            )
        }
    }

    private suspend fun saveSyncedTransaction(
        local: TransactionEntity,
        response: TransactionMutationResponseDto
    ) {
        val current = transactionDao.getByLocalId(local.localId)?.transaction ?: return
        if (current.updatedAtEpochMillis > local.updatedAtEpochMillis) {
            transactionDao.update(
                current.copy(
                    serverId = requireNotNull(response.id),
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )
            return
        }
        transactionDao.update(
            local.copy(
                serverId = requireNotNull(response.id),
                amount = response.amount ?: local.amount,
                transactionDateEpochMillis = response.transactionDate
                    ?.let(Instant::parse)
                    ?.toEpochMilli()
                    ?: local.transactionDateEpochMillis,
                comment = response.comment,
                createdAtEpochMillis = response.createdAt
                    ?.let(Instant::parse)
                    ?.toEpochMilli()
                    ?: local.createdAtEpochMillis,
                updatedAtEpochMillis = response.updatedAt
                    ?.let(Instant::parse)
                    ?.toEpochMilli()
                    ?: clock.millis(),
                syncStatus = SyncStatus.SYNCED
            )
        )
    }

    private suspend fun saveSyncedTransaction(
        local: TransactionEntity,
        remote: Transaction
    ) {
        val current = transactionDao.getByLocalId(local.localId)?.transaction ?: return
        if (current.updatedAtEpochMillis > local.updatedAtEpochMillis) {
            transactionDao.update(
                current.copy(
                    serverId = requireNotNull(remote.serverId),
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )
            return
        }
        transactionDao.update(
            local.copy(
                serverId = requireNotNull(remote.serverId),
                amount = remote.amount.toPlainString(),
                transactionDateEpochMillis = remote.transactionDate.toEpochMilli(),
                comment = remote.comment,
                createdAtEpochMillis = remote.createdAt.toEpochMilli(),
                updatedAtEpochMillis = remote.updatedAt.toEpochMilli(),
                syncStatus = SyncStatus.SYNCED
            )
        )
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

    private suspend fun saveSyncedAccount(
        sentAccount: AccountEntity,
        remoteAccount: Account
    ) {
        val current = accountDao.getByLocalId(sentAccount.localId) ?: return
        if (current.updatedAtEpochMillis > sentAccount.updatedAtEpochMillis) {
            accountDao.update(
                current.copy(
                    serverId = requireNotNull(remoteAccount.serverId),
                    userId = remoteAccount.userId,
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )
            return
        }
        accountDao.markSynced(
            localId = sentAccount.localId,
            serverId = requireNotNull(remoteAccount.serverId),
            userId = remoteAccount.userId,
            name = remoteAccount.name,
            emoji = remoteAccount.emoji,
            balance = remoteAccount.balance.toPlainString(),
            currency = remoteAccount.currency,
            createdAt = remoteAccount.createdAt.toEpochMilli(),
            updatedAt = remoteAccount.updatedAt.toEpochMilli()
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
        const val ACCOUNT_CLOCK_SKEW_MILLIS = 5 * 60 * 1000L
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
