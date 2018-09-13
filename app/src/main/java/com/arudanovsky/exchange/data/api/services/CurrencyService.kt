package com.arudanovsky.exchange.data.api.services

import com.arudanovsky.exchange.data.api.model.RatesDto
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    fun getRates(@Query("base") currencyKey: String): Observable<RatesDto>
}