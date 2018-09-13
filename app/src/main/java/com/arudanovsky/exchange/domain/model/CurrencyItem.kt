package com.arudanovsky.exchange.domain.model

import java.math.BigDecimal

data class CurrencyItem (
    val key: String,
    val rate: BigDecimal = BigDecimal.ZERO
)