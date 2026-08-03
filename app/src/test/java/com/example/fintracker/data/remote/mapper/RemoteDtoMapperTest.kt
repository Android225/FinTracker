package com.example.fintracker.data.remote.mapper

import com.example.fintracker.data.remote.dto.AccountBriefDto
import com.example.fintracker.data.remote.dto.AccountDto
import com.example.fintracker.data.remote.dto.CategoryDto
import com.example.fintracker.data.remote.dto.TransactionResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class RemoteDtoMapperTest {
    @Test
    fun `maps account dto to validated domain model`() {
        val account = AccountDto(
            id = 1,
            userId = 2,
            name = "Основной счёт",
            emoji = "💵",
            balance = "1000.50",
            currency = "RUB",
            createdAt = "2026-07-19T10:00:00Z",
            updatedAt = "2026-07-19T11:00:00+00:00"
        ).toDomain()

        assertEquals(BigDecimal("1000.50"), account.balance)
        assertEquals("💵", account.emoji)
        assertEquals(Instant.parse("2026-07-19T11:00:00Z"), account.updatedAt)
    }

    @Test
    fun `maps transaction date with offset to instant`() {
        val transaction = transactionDto(
            amount = "500.00",
            transactionDate = "2026-07-19T13:00:00+03:00"
        ).toDomain()

        assertEquals(BigDecimal("500.00"), transaction.amount)
        assertEquals(Instant.parse("2026-07-19T10:00:00Z"), transaction.transactionDate)
    }

    @Test
    fun `rejects invalid amount before it reaches domain`() {
        assertThrows(RemoteDataException::class.java) {
            transactionDto(amount = "not-a-number").toDomain()
        }
    }

    private fun transactionDto(
        amount: String,
        transactionDate: String = "2026-07-19T10:00:00Z"
    ): TransactionResponseDto = TransactionResponseDto(
        id = 1,
        account = AccountBriefDto(1, "Основной счёт", "💵", "1000.00", "RUB"),
        category = CategoryDto(1, "Зарплата", "💰", true),
        amount = amount,
        transactionDate = transactionDate,
        comment = null,
        createdAt = "2026-07-19T10:00:00Z",
        updatedAt = "2026-07-19T10:00:00Z"
    )
}
