package com.example.fintracker.presentation.screens.analytics

import androidx.lifecycle.SavedStateHandle
import com.example.fintracker.data.repository.FixedExchangeRatesRepository
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountDraft
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.TransactionDraft
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.domain.usecase.GetAnalyticsUseCase
import com.example.fintracker.domain.usecase.GetTransactionsUseCase
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `default period starts on first day of current month`() =
        runTest(mainDispatcherRule.dispatcher) {
            val accountsRepository = FakeAccountsRepository()
            val transactionsUseCase = GetTransactionsUseCase(
                accountsRepository = accountsRepository,
                transactionsRepository = EmptyTransactionsRepository,
                defaultDispatcher = mainDispatcherRule.dispatcher
            )
            val analyticsUseCase = GetAnalyticsUseCase(
                accountsRepository = accountsRepository,
                categoriesRepository = EmptyCategoriesRepository,
                getTransactions = transactionsUseCase,
                defaultDispatcher = mainDispatcherRule.dispatcher
            )

            val viewModel = AnalyticsViewModel(
                savedStateHandle = SavedStateHandle(),
                getAnalytics = analyticsUseCase,
                exchangeRatesRepository = FixedExchangeRatesRepository(),
                clock = Clock.fixed(Instant.parse("2026-07-19T12:00:00Z"), ZoneOffset.UTC),
                defaultDispatcher = mainDispatcherRule.dispatcher
            )
            viewModel.onEvent(AnalyticsEvent.ScreenVisible)

            val content = (viewModel.uiState.value.loadState as UiState.Empty).data!!
            assertEquals(LocalDate.of(2026, 7, 1), content.selectedPeriod.start)
            assertEquals(LocalDate.of(2026, 7, 19), content.selectedPeriod.end)
        }

    private class FakeAccountsRepository : AccountsRepository {
        override suspend fun getAccounts(): DataResult<List<Account>> = DataResult.Success(
            listOf(
                Account(
                    id = 1L,
                    serverId = 1,
                    userId = 1,
                    name = "Main",
                    emoji = "",
                    balance = BigDecimal.ZERO,
                    currency = "RUB",
                    createdAt = Instant.EPOCH,
                    updatedAt = Instant.EPOCH
                )
            )
        )

        override fun observeAccounts(): Flow<List<Account>> = flowOf(emptyList())

        override suspend fun getAccount(id: Long): DataResult<Account> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun saveAccount(id: Long?, draft: AccountDraft): DataResult<Long> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun deleteAccount(id: Long): DataResult<Unit> =
            DataResult.Failure(AppError.BadRequest)
    }

    private data object EmptyCategoriesRepository : CategoriesRepository {
        override suspend fun getCategories(): DataResult<List<Category>> =
            DataResult.Success(emptyList())

        override fun observeCategories(): Flow<List<Category>> = flowOf(emptyList())

        override suspend fun getCategories(isIncome: Boolean): DataResult<List<Category>> =
            DataResult.Success(emptyList())
    }

    private data object EmptyTransactionsRepository : TransactionsRepository {
        override suspend fun getTransactionsForPeriod(
            accountId: Long,
            startDate: LocalDate,
            endDate: LocalDate
        ): DataResult<List<Transaction>> = DataResult.Success(emptyList())

        override suspend fun getTransaction(id: Long): DataResult<Transaction> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun saveTransaction(
            id: Long?,
            draft: TransactionDraft
        ): DataResult<Long> = DataResult.Failure(AppError.BadRequest)

        override suspend fun deleteTransaction(id: Long): DataResult<Unit> =
            DataResult.Failure(AppError.BadRequest)
    }
}
