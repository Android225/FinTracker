package com.example.fintracker.domain.usecase

import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.domain.result.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate

class GetTransactionsByTypeUseCase(
    private val getTransactionsForPeriod: GetTransactionsForPeriodUseCase,
    private val defaultDispatcher: CoroutineDispatcher
) {
    // Отделяет доходы от расходов после объединения транзакций всех счетов.
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate,
        isIncome: Boolean
    ): DataResult<List<Transaction>> {
        val result = getTransactionsForPeriod(
            startDate = startDate,
            endDate = endDate
        )
        return withContext(defaultDispatcher) {
            result.map { transactions ->
                transactions.filter { it.category.isIncome == isIncome }
            }
        }
    }
}
