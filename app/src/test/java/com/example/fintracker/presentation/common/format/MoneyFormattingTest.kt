package com.example.fintracker.presentation.common.format

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class MoneyFormattingTest {
    @Test
    fun `different currencies are shown as separate totals`() {
        val values = listOf(
            BigDecimal("100") to "RUB",
            BigDecimal("50") to "RUB",
            BigDecimal("3") to "USD"
        )

        assertEquals(
            listOf(
                MoneyTotalUi("RUB", "150 ₽"),
                MoneyTotalUi("USD", "3 $")
            ),
            values.moneyTotalsByCurrency(
                Pair<BigDecimal, String>::first,
                Pair<BigDecimal, String>::second
            )
        )
    }

    @Test
    fun `currency codes are normalized before formatting`() {
        assertEquals("$", currencySymbol(" usd "))
    }
}
