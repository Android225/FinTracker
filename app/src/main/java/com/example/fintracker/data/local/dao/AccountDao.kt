package com.example.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fintracker.data.local.entity.AccountEntity
import com.example.fintracker.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY localId")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM accounts ORDER BY localId")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE localId = :localId")
    suspend fun getByLocalId(localId: Long): AccountEntity?

    @Query("SELECT * FROM accounts WHERE serverId = :serverId")
    suspend fun getByServerId(serverId: Int): AccountEntity?

    @Query("SELECT * FROM accounts WHERE syncStatus != 'SYNCED' ORDER BY localId")
    suspend fun getPending(): List<AccountEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Query(
        """
        UPDATE accounts
        SET serverId = :serverId,
            userId = :userId,
            name = :name,
            emoji = :emoji,
            balance = :balance,
            currency = :currency,
            createdAtEpochMillis = :createdAt,
            updatedAtEpochMillis = :updatedAt,
            syncStatus = :syncStatus
        WHERE localId = :localId
        """
    )
    suspend fun markSynced(
        localId: Long,
        serverId: Int,
        userId: Int?,
        name: String,
        emoji: String,
        balance: String,
        currency: String,
        createdAt: Long,
        updatedAt: Long,
        syncStatus: SyncStatus = SyncStatus.SYNCED
    )
}
