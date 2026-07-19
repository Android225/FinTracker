package com.example.fintracker.presentation.common.format

import java.math.BigDecimal
import java.text.DecimalFormat

internal fun BigDecimal.formatMoney(): String =
    "${DecimalFormat("#,###").format(this).replace(',', ' ')} ₽"
