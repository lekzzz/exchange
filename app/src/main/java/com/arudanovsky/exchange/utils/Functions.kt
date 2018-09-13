package com.arudanovsky.exchange.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

fun formatMultipliedDecimals(decimal1: BigDecimal, decimal2: BigDecimal) = String.format(
    Locale.getDefault(),
    decimal1
        .multiply(decimal2)
        .setScale(2, RoundingMode.HALF_UP).toString(),
    null
)