package com.example.fintracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.fintracker.data.local.dao.AccountDao
import com.example.fintracker.data.local.dao.CategoryDao
import com.example.fintracker.data.local.dao.TransactionDao
import com.example.fintracker.data.local.entity.AccountEntity
import com.example.fintracker.data.local.entity.CategoryEntity
import com.example.fintracker.data.local.entity.SyncStatus
import com.example.fintracker.data.local.entity.TransactionEntity

@Database(
    entities = [AccountEntity::class, CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(SyncStatusConverter::class)
abstract class FinTrackerDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}

class SyncStatusConverter {
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}
