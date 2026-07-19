package com.example.fintracker.domain.usecase

import com.example.fintracker.domain.Account
import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.analytics.AnalyticsCalculator
import com.example.fintracker.domain.analytics.model.AnalyticsData
import com.example.fintracker.domain.analytics.model.AnalyticsFilter
import com.example.fintracker.domain.repository.AccountsRepository
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.time.ZoneId

class GetAnalyticsUseCase(
    private val accountsRepository: AccountsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val getTransactions: GetTransactionsUseCase,
    private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        filter: AnalyticsFilter,
        zoneId: ZoneId
    ): DataResult<AnalyticsData> = withContext(defaultDispatcher) {
        // Независимые справочники загружаются параллельно, чтобы сократить ожидание экрана.
        val (accountsResult, categoriesResult) = coroutineScope {
            val accounts = async { accountsRepository.getAccounts() }
            val categories = async { categoriesRepository.getCategories() }
            accounts.await() to categories.await()
        }

        val accounts = when (accountsResult) {
            is DataResult.Success -> accountsResult.data
            is DataResult.Failure -> return@withContext accountsResult
        }
        val categories = when (categoriesResult) {
            is DataResult.Success -> categoriesResult.data
            is DataResult.Failure -> return@withContext categoriesResult
        }

        // При выбранном счёте не запрашиваем транзакции остальных счетов.
        val availableAccountIds = accounts.mapTo(linkedSetOf(), Account::id)
        val requestedAccountIds = filter.accountId
            ?.takeIf(availableAccountIds::contains)
            ?.let(::setOf)
            ?: availableAccountIds
        val transactions = when (
            val result = getTransactions(
                type = filter.type,
                startDate = filter.period.start,
                endDate = filter.period.end,
                accountIds = requestedAccountIds
            )
        ) {
            is DataResult.Success -> result.data
            is DataResult.Failure -> return@withContext result
        }

        DataResult.Success(
            AnalyticsData(
                accounts = accounts.map(Account::toBrief),
                categories = categories,
                calculation = AnalyticsCalculator.calculate(
                    transactions = transactions,
                    filter = filter,
                    zoneId = zoneId
                )
            )
        )
    }
}

private fun Account.toBrief(): AccountBrief = AccountBrief(
    id = id,
    name = name,
    emoji = emoji,
    balance = balance,
    currency = currency
)
