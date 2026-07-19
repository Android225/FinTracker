package com.example.fintracker.di

import com.example.fintracker.di.qualifier.DefaultDispatcher
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.usecase.GetTransactionsForPeriodUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetTransactionsForPeriodUseCase(
        accountsRepository: AccountsRepository,
        transactionsRepository: TransactionsRepository,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): GetTransactionsForPeriodUseCase = GetTransactionsForPeriodUseCase(
        accountsRepository = accountsRepository,
        transactionsRepository = transactionsRepository,
        defaultDispatcher = defaultDispatcher
    )
}
