package com.example.fintracker.presentation.screens.accounts

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountDraft
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.data.repository.FixedExchangeRatesRepository
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.common.format.MoneyTotalUi
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsEvent
import com.example.fintracker.test.MainDispatcherRule
import com.example.fintracker.test.FakeSettingsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `manual refresh replaces active load and later events read local data`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = BlockingAccountsRepository()
            val viewModel = AccountsViewModel(
                accountsRepository = repository,
                settingsRepository = FakeSettingsRepository(),
                exchangeRatesRepository = FixedExchangeRatesRepository(),
                clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC),
                defaultDispatcher = mainDispatcherRule.dispatcher
            )

            viewModel.onEvent(AccountsEvent.ScreenVisible)
            viewModel.onEvent(AccountsEvent.Refresh)
            assertEquals(2, repository.requestCount)

            repository.complete(DataResult.Success(emptyList()))
            assertEquals(
                UiState.Empty(
                    AccountsContent(
                        totalBalances = listOf(MoneyTotalUi("RUB", "0 ₽")),
                        accounts = emptyList()
                    )
                ),
                viewModel.uiState.value.loadState
            )

            viewModel.onEvent(AccountsEvent.ScreenVisible)
            assertEquals(3, repository.requestCount)

            viewModel.onEvent(AccountsEvent.Refresh)
            assertEquals(4, repository.requestCount)
        }

    @Test
    fun `initial failure becomes screen error`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel(
            SequentialAccountsRepository(DataResult.Failure(AppError.Network))
        )
        viewModel.onEvent(AccountsEvent.ScreenVisible)

        assertEquals(
            UiState.Error(AppError.Network),
            viewModel.uiState.value.loadState
        )
    }

    @Test
    fun `refresh failure preserves content and emits app error`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = SequentialAccountsRepository(
                DataResult.Success(listOf(account())),
                DataResult.Failure(AppError.Server)
            )
            val viewModel = createViewModel(repository)
            viewModel.onEvent(AccountsEvent.ScreenVisible)
            val loadedState = viewModel.uiState.value.loadState

            viewModel.onEvent(AccountsEvent.Refresh)

            assertEquals(loadedState, viewModel.uiState.value.loadState)
            assertEquals(false, viewModel.uiState.value.isRefreshing)
            assertEquals(AppError.Server, viewModel.errors.first())
        }

    private fun createViewModel(repository: AccountsRepository): AccountsViewModel =
        AccountsViewModel(
            accountsRepository = repository,
            settingsRepository = FakeSettingsRepository(),
            exchangeRatesRepository = FixedExchangeRatesRepository(),
            clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC),
            defaultDispatcher = mainDispatcherRule.dispatcher
        )

    private class SequentialAccountsRepository(
        vararg results: DataResult<List<Account>>
    ) : AccountsRepository {
        private val results = results.toMutableList()

        override suspend fun getAccounts(): DataResult<List<Account>> = results.removeFirst()

        override fun observeAccounts(): Flow<List<Account>> = flowOf(emptyList())

        override suspend fun getAccount(id: Long): DataResult<Account> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun saveAccount(id: Long?, draft: AccountDraft): DataResult<Long> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun deleteAccount(id: Long): DataResult<Unit> =
            DataResult.Failure(AppError.BadRequest)
    }

    private class BlockingAccountsRepository : AccountsRepository {
        private val firstResult = CompletableDeferred<DataResult<List<Account>>>()
        var requestCount: Int = 0
            private set

        override suspend fun getAccounts(): DataResult<List<Account>> {
            requestCount++
            return firstResult.await()
        }

        override fun observeAccounts(): Flow<List<Account>> = flowOf(emptyList())

        override suspend fun getAccount(id: Long): DataResult<Account> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun saveAccount(id: Long?, draft: AccountDraft): DataResult<Long> =
            DataResult.Failure(AppError.BadRequest)

        override suspend fun deleteAccount(id: Long): DataResult<Unit> =
            DataResult.Failure(AppError.BadRequest)

        fun complete(result: DataResult<List<Account>>) {
            firstResult.complete(result)
        }
    }

    private fun account(): Account = Account(
        id = 1L,
        serverId = 1,
        userId = 1,
        name = "Main",
        emoji = "💳",
        balance = BigDecimal("100"),
        currency = "RUB",
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )
}
