package com.example.fintracker.presentation.screens.analytics

import androidx.lifecycle.SavedStateHandle
import com.example.fintracker.domain.Account
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.repository.TransactionsRepository
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.domain.usecase.GetAnalyticsUseCase
import com.example.fintracker.domain.usecase.GetTransactionsUseCase
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
                clock = Clock.fixed(Instant.parse("2026-07-19T12:00:00Z"), ZoneOffset.UTC),
                defaultDispatcher = mainDispatcherRule.dispatcher
            )

            val content = (viewModel.uiState.value.loadState as UiState.Empty).data!!
            assertEquals(LocalDate.of(2026, 7, 1), content.selectedPeriod.start)
            assertEquals(LocalDate.of(2026, 7, 19), content.selectedPeriod.end)
        }

    private class FakeAccountsRepository : AccountsRepository {
        override suspend fun getAccounts(): DataResult<List<Account>> = DataResult.Success(
            listOf(
                Account(
                    id = 1,
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
    }

    private data object EmptyCategoriesRepository : CategoriesRepository {
        override suspend fun getCategories(): DataResult<List<Category>> =
            DataResult.Success(emptyList())
    }

    private data object EmptyTransactionsRepository : TransactionsRepository {
        override suspend fun getTransactionsForPeriod(
            accountId: Int,
            startDate: LocalDate,
            endDate: LocalDate
        ): DataResult<List<Transaction>> = DataResult.Success(emptyList())
    }
}
