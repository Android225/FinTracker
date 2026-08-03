package com.example.fintracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["localId"],
            childColumns = ["accountLocalId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"]
        )
    ],
    indices = [
        Index(value = ["serverId"], unique = true),
        Index("accountLocalId"),
        Index("categoryId"),
        Index("transactionDateEpochMillis")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Int?,
    val accountLocalId: Long,
    val categoryId: Int,
    val amount: String,
    val transactionDateEpochMillis: Long,
    val comment: String?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val syncStatus: SyncStatus
)
