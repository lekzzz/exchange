package com.arudanovsky.exchange.utils

import java.math.BigDecimal

fun CharSequence?.toBigDecimal() =
    if (isNullOrBlank()) BigDecimal.ZERO else BigDecimal(toString())