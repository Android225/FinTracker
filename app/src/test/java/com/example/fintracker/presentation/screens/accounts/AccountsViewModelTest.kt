package com.example.fintracker.presentation.screens.accounts

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsEvent
import com.example.fintracker.test.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
    fun `visible event is throttled while manual refresh bypasses freshness`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = BlockingAccountsRepository()
            val viewModel = AccountsViewModel(
                accountsRepository = repository,
                clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC),
                defaultDispatcher = mainDispatcherRule.dispatcher
            )

            viewModel.onEvent(AccountsEvent.ScreenVisible)
            viewModel.onEvent(AccountsEvent.Refresh)
            assertEquals(1, repository.requestCount)

            repository.complete(DataResult.Success(emptyList()))
            assertEquals(
                UiState.Empty(AccountsContent(totalBalance = "0 ₽", accounts = emptyList())),
                viewModel.uiState.value.loadState
            )

            viewModel.onEvent(AccountsEvent.ScreenVisible)
            assertEquals(1, repository.requestCount)

            viewModel.onEvent(AccountsEvent.Refresh)
            assertEquals(2, repository.requestCount)
        }

    @Test
    fun `initial failure becomes screen error`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel(
            SequentialAccountsRepository(DataResult.Failure(AppError.Network))
        )

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
            val loadedState = viewModel.uiState.value.loadState

            viewModel.onEvent(AccountsEvent.Refresh)

            assertEquals(loadedState, viewModel.uiState.value.loadState)
            assertEquals(false, viewModel.uiState.value.isRefreshing)
            assertEquals(AppError.Server, viewModel.errors.first())
        }

    private fun createViewModel(repository: AccountsRepository): AccountsViewModel =
        AccountsViewModel(
            accountsRepository = repository,
            clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC),
            defaultDispatcher = mainDispatcherRule.dispatcher
        )

    private class SequentialAccountsRepository(
        vararg results: DataResult<List<Account>>
    ) : AccountsRepository {
        private val results = results.toMutableList()

        override suspend fun getAccounts(): DataResult<List<Account>> = results.removeFirst()
    }

    private class BlockingAccountsRepository : AccountsRepository {
        private val firstResult = CompletableDeferred<DataResult<List<Account>>>()
        var requestCount: Int = 0
            private set

        override suspend fun getAccounts(): DataResult<List<Account>> {
            requestCount++
            return firstResult.await()
        }

        fun complete(result: DataResult<List<Account>>) {
            firstResult.complete(result)
        }
    }

    private fun account(): Account = Account(
        id = 1,
        userId = 1,
        name = "Main",
        emoji = "💳",
        balance = BigDecimal("100"),
        currency = "RUB",
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )
}
