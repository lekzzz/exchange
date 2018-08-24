package com.arudanovsky.exchange

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var retrofit: Retrofit? = null
    fun getRetrofitClient() : ApiService {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://revolut.duckdns.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .client(provideOkHttpClient())
                .validateEagerly(true)
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
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

    private fun provideGson() = GsonBuilder()
        .registerTypeAdapter(RatesListDto::class.java, RateDtoDeserializer())
        .create()
}