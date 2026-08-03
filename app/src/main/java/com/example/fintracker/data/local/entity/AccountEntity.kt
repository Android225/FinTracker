package com.example.fintracker.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    indices = [Index(value = ["serverId"], unique = true)]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Int?,
    val userId: Int?,
    val name: String,
    val emoji: String,
    val balance: String,
    val currency: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val syncStatus: SyncStatus
)
