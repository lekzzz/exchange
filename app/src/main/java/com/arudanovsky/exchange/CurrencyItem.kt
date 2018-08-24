package com.arudanovsky.exchange

import java.math.BigDecimal

data class CurrencyItem (
    val key: String,
    val rate: BigDecimal = BigDecimal.ZERO
)