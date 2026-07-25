package com.example.fintracker.presentation.screens.editor

import com.example.fintracker.domain.Account
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class TransactionEditorStateTest {
    @Test
    fun `amount currency follows selected account`() {
        val rubAccount = account(id = 1, currency = "RUB")
        val usdAccount = account(id = 2, currency = "USD")
        val state = TransactionEditorState(
            accountId = usdAccount.id,
            accounts = listOf(rubAccount, usdAccount)
        )

        assertEquals("USD", state.selectedAccountCurrency)
    }

    private fun account(id: Long, currency: String): Account = Account(
        id = id,
        serverId = id.toInt(),
        userId = 1,
        name = currency,
        emoji = "💳",
        balance = BigDecimal.ZERO,
        currency = currency,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )
}
