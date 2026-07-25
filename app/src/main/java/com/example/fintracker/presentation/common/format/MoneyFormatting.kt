package com.example.fintracker.presentation.common.format

import java.math.BigDecimal
import java.text.DecimalFormat

internal fun BigDecimal.formatMoney(currency: String = "RUB"): String =
    "${DecimalFormat("#,###.##").format(this).replace(',', ' ')} ${currencySymbol(currency)}"

internal fun <T> Iterable<T>.formatMoneyByCurrency(
    amount: (T) -> BigDecimal,
    currency: (T) -> String
): String {
    val items = toList()
    if (items.isEmpty()) return BigDecimal.ZERO.formatMoney()
    return items
        .groupBy(currency)
        .entries
        .joinToString(" · ") { (code, currencyItems) ->
            currencyItems.sumOf(amount).formatMoney(code)
        }
}

internal fun currencySymbol(currency: String): String = when (currency) {
    "RUB" -> "₽"
    "USD" -> "$"
    "EUR" -> "€"
    else -> currency
}
