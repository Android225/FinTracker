package com.example.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fintracker.data.local.entity.SyncStatus
import com.example.fintracker.data.local.entity.TransactionEntity
import com.example.fintracker.data.local.entity.TransactionRecord

@Dao
interface TransactionDao {
    @Transaction
    @Query(
        """
        SELECT * FROM transactions
        WHERE accountLocalId = :accountId
          AND transactionDateEpochMillis BETWEEN :startMillis AND :endMillis
        ORDER BY transactionDateEpochMillis DESC
        """
    )
    suspend fun getForPeriod(
        accountId: Long,
        startMillis: Long,
        endMillis: Long
    ): List<TransactionRecord>

    @Transaction
    @Query("SELECT * FROM transactions WHERE localId = :localId")
    suspend fun getByLocalId(localId: Long): TransactionRecord?

    @Query("SELECT * FROM transactions WHERE serverId = :serverId")
    suspend fun getEntityByServerId(serverId: Int): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE syncStatus != 'SYNCED' ORDER BY localId")
    suspend fun getPending(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)
}
