package com.example.fintracker.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class FixedExchangeRatesRepositoryTest {
    private val repository = FixedExchangeRatesRepository()

    @Test
    fun `converts between supported currencies through ruble rates`() {
        assertAmount("180", repository.convert(BigDecimal("2"), "USD", "RUB"))
        assertAmount("2", repository.convert(BigDecimal("200"), "RUB", "EUR"))
        assertAmount("0.9", repository.convert(BigDecimal.ONE, "USD", "EUR"))
    }

    @Test
    fun `normalizes currency codes`() {
        assertAmount("90", repository.convert(BigDecimal.ONE, " usd ", "rub"))
    }

    @Test
    fun `supports every currency available in settings`() {
        assertEquals(listOf("RUB", "USD", "EUR", "GBP", "CNY"), repository.supportedCurrencies)
        assertAmount("115", repository.convert(BigDecimal.ONE, "GBP", "RUB"))
        assertAmount("12.5", repository.convert(BigDecimal.ONE, "CNY", "RUB"))
    }

    private fun assertAmount(expected: String, actual: BigDecimal) {
        assertEquals(0, BigDecimal(expected).compareTo(actual))
    }
}
