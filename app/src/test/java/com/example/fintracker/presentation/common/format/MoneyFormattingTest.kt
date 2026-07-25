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
            "150 ₽ · 3 $",
            values.formatMoneyByCurrency(Pair<BigDecimal, String>::first, Pair<BigDecimal, String>::second)
        )
    }
}
