package com.example.fintracker.data.repository

import com.example.fintracker.domain.repository.ExchangeRatesRepository
import java.math.BigDecimal
import java.math.MathContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FixedExchangeRatesRepository @Inject constructor() : ExchangeRatesRepository {
    override val supportedCurrencies: List<String> = RATES_TO_RUB.keys.toList()

    override fun convert(
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String
    ): BigDecimal {
        val sourceRate = rateToRub(fromCurrency)
        val targetRate = rateToRub(toCurrency)
        return amount.multiply(sourceRate).divide(targetRate, MathContext.DECIMAL64)
    }

    private fun rateToRub(currency: String): BigDecimal =
        requireNotNull(RATES_TO_RUB[normalize(currency)]) {
            "Unsupported currency: $currency"
        }

    private fun normalize(currency: String): String =
        currency.trim().uppercase(Locale.ROOT)

    private companion object {
        // Временная заглушка. Позже эти значения можно получать из сетевой реализации репозитория.
        val RATES_TO_RUB = linkedMapOf(
            "RUB" to BigDecimal.ONE,
            "USD" to BigDecimal("90"),
            "EUR" to BigDecimal("100"),
            "GBP" to BigDecimal("115"),
            "CNY" to BigDecimal("12.5")
        )
    }
}
