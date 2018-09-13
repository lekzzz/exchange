package com.arudanovsky.exchange.data.api.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.*

data class RatesDto (
    @SerializedName("base") val baseKey: String,
    @SerializedName("date") val date: String,
    @SerializedName("rates") val rates: TreeMap<String, BigDecimal>
)