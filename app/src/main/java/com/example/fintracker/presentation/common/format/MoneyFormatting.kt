package com.example.fintracker.presentation.common.format

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale

data class MoneyTotalUi(
    val currency: String,
    val formattedAmount: String
)

internal fun BigDecimal.formatMoney(currency: String = "RUB"): String =
    "${DecimalFormat("#,###.##").format(this).replace(',', ' ')} ${currencySymbol(currency)}"

internal fun <T> Iterable<T>.moneyTotalsByCurrency(
    amount: (T) -> BigDecimal,
    currency: (T) -> String
): List<MoneyTotalUi> {
    val items = toList()
    if (items.isEmpty()) {
        return listOf(MoneyTotalUi(currency = "RUB", formattedAmount = BigDecimal.ZERO.formatMoney()))
    }
    return items
        .groupBy { item -> normalizeCurrency(currency(item)) }
        .map { (code, currencyItems) ->
            MoneyTotalUi(
                currency = code,
                formattedAmount = currencyItems.sumOf(amount).formatMoney(code)
            )
        }
}

internal fun currencySymbol(currency: String): String = when (normalizeCurrency(currency)) {
    "RUB" -> "₽"
    "USD" -> "$"
    "EUR" -> "€"
    else -> normalizeCurrency(currency)
}

internal fun normalizeCurrency(currency: String): String =
    currency.trim().uppercase(Locale.ROOT)
