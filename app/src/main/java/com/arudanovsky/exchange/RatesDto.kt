package com.arudanovsky.exchange

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RatesDto (
    @SerializedName("base") val baseKey: String,
    @SerializedName("date") val date: String,
    @SerializedName("rates") val rates: RatesListDto
)

data class RatesListDto(
    val list: List<RateDto>
)

data class RateDto(
    val key: String,
    val rate: BigDecimal
)