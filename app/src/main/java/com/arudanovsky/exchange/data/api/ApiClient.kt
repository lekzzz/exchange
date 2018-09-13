package com.arudanovsky.exchange.data.api

import android.util.Log
import com.arudanovsky.exchange.data.api.services.CurrencyService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var retrofit: Retrofit? = null
    fun getRetrofitClient() : CurrencyService {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .client(provideOkHttpClient())
                .validateEagerly(true)
                .build()
        }
        return retrofit!!.create(CurrencyService::class.java)
    }

    private fun provideOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(provideLoggingInterseptor())
            .build()

    private fun provideLoggingInterseptor() : HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            Log.d("OKHTTP", message)
        })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    private fun provideGson() = GsonBuilder().create()
}