package com.example.fintracker.domain.settings

enum class AppCurrency(val code: String) {
    Rub("RUB"),
    Usd("USD"),
    Eur("EUR"),
    Gbp("GBP"),
    Cny("CNY");

    companion object {
        fun fromCode(code: String): AppCurrency =
            entries.firstOrNull { it.code == code.uppercase() } ?: Rub
    }
}
