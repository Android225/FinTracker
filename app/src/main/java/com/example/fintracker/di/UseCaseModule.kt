package com.example.fintracker.di

import com.example.fintracker.di.qualifier.DefaultDispatcher
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.usecase.GetAnalyticsUseCase
import com.example.fintracker.domain.usecase.GetTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetTransactionsUseCase(
        accountsRepository: AccountsRepository,
        transactionsRepository: TransactionsRepository,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): GetTransactionsUseCase = GetTransactionsUseCase(
        accountsRepository = accountsRepository,
        transactionsRepository = transactionsRepository,
        defaultDispatcher = defaultDispatcher
    )

    @Provides
    fun provideGetAnalyticsUseCase(
        accountsRepository: AccountsRepository,
        categoriesRepository: CategoriesRepository,
        getTransactions: GetTransactionsUseCase,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): GetAnalyticsUseCase = GetAnalyticsUseCase(
        accountsRepository = accountsRepository,
        categoriesRepository = categoriesRepository,
        getTransactions = getTransactions,
        defaultDispatcher = defaultDispatcher
    )
}
