package com.example.fintracker.di

import android.content.Context
import androidx.room.Room
import com.example.fintracker.data.local.FinTrackerDatabase
import com.example.fintracker.data.local.dao.AccountDao
import com.example.fintracker.data.local.dao.CategoryDao
import com.example.fintracker.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinTrackerDatabase =
        Room.databaseBuilder(
            context,
            FinTrackerDatabase::class.java,
            "fintracker.db"
        ).build()

    @Provides
    fun provideAccountDao(database: FinTrackerDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideCategoryDao(database: FinTrackerDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: FinTrackerDatabase): TransactionDao =
        database.transactionDao()
}
