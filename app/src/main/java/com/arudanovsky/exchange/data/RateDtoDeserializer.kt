package com.arudanovsky.exchange.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class RateDtoDeserializer: JsonDeserializer<RatesListDto> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        je: JsonElement, type: Type,
        jdc: JsonDeserializationContext
    ): RatesListDto? {
        if (je.isJsonPrimitive) {
            return null
        }
        val rates = emptyList<RateDto>().toMutableList()
        je.asJsonObject.keySet().forEach {
            rates.add(
                RateDto(
                    it,
                    je.asJsonObject.get(it).asBigDecimal
                )
            )
        }

        return RatesListDto(rates)
    }
}