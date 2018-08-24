package com.arudanovsky.exchange

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("latest")
    fun getRates(@Query("base") currencyKey: String): Observable<RatesDto>
}