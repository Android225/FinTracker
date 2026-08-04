package com.example.fintracker.di

import com.example.fintracker.data.repository.AccountsRepositoryImpl
import com.example.fintracker.data.repository.CategoriesRepositoryImpl
import com.example.fintracker.data.repository.FixedExchangeRatesRepository
import com.example.fintracker.data.repository.TransactionsRepositoryImpl
import com.example.fintracker.data.repository.SettingsRepositoryImpl
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.repository.ExchangeRatesRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAccountsRepository(
        implementation: AccountsRepositoryImpl
    ): AccountsRepository

    @Binds
    @Singleton
    abstract fun bindCategoriesRepository(
        implementation: CategoriesRepositoryImpl
    ): CategoriesRepository

    @Binds
    @Singleton
    abstract fun bindTransactionsRepository(
        implementation: TransactionsRepositoryImpl
    ): TransactionsRepository

    @Binds
    @Singleton
    abstract fun bindExchangeRatesRepository(
        implementation: FixedExchangeRatesRepository
    ): ExchangeRatesRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        implementation: SettingsRepositoryImpl
    ): SettingsRepository
}
