package com.example.fintracker.domain.repository

import java.math.BigDecimal

interface ExchangeRatesRepository {
    val supportedCurrencies: List<String>

    fun convert(
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String
    ): BigDecimal
}
