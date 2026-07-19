package com.example.fintracker.presentation.screens.accounts

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.screens.accounts.model.AccountsContent
import com.example.fintracker.presentation.screens.accounts.model.AccountsEvent
import com.example.fintracker.test.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
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
                viewModel.uiState.value.dataState
            )

            viewModel.onEvent(AccountsEvent.ScreenVisible)
            assertEquals(1, repository.requestCount)

            viewModel.onEvent(AccountsEvent.Refresh)
            assertEquals(2, repository.requestCount)
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
}
