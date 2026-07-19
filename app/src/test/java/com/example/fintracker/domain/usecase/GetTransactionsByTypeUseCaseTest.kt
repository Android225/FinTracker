package com.example.fintracker.domain.usecase

import com.example.fintracker.domain.AccountBrief
import com.example.fintracker.domain.Category
import com.example.fintracker.domain.Transaction
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class GetTransactionsByTypeUseCaseTest {
    @Test
    fun `keeps only requested transaction type`() = runBlocking {
        val transactions = listOf(
            transaction(id = 1, isIncome = false),
            transaction(id = 2, isIncome = true)
        )
        val periodUseCase = FakePeriodUseCaseFactory.create(transactions)
        val useCase = GetTransactionsByTypeUseCase(periodUseCase, Dispatchers.Unconfined)

        val result = useCase(DATE, DATE, isIncome = true)

        assertEquals(listOf(2), (result as DataResult.Success).data.map(Transaction::id))
    }

    private fun transaction(id: Int, isIncome: Boolean): Transaction = Transaction(
        id = id,
        account = AccountBrief(1, "Account", "", BigDecimal.ZERO, "RUB"),
        category = Category(id, "Category", "", isIncome),
        amount = BigDecimal.ONE,
        transactionDate = Instant.EPOCH,
        comment = null,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )

    private companion object {
        val DATE: LocalDate = LocalDate.of(2026, 7, 19)
    }
}
