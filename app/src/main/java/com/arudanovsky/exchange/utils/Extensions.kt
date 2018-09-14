package com.arudanovsky.exchange.utils

import io.reactivex.Observable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

fun CharSequence?.toBigDecimal() =
    if (isNullOrBlank()) BigDecimal.ZERO else BigDecimal(toString())

fun <T> Observable<T>.exponentialBackoff(
    retryTriesCount: Int = 3,
    delayMultiplexer: Double = 2.0
): Observable<T> {
    return this.retryWhen { errors: Observable<Throwable> ->
        errors
            .zipWith(1..retryTriesCount) { lastError: Throwable, currentRetryNumber: Int ->
                return@zipWith currentRetryNumber to lastError
            }
            .flatMap<Any> { (currentTryNumber: Int, lastError: Throwable) ->
                if (currentTryNumber == retryTriesCount)
                    Observable.error(lastError)
                else
                    Observable.timer(
                        Math.pow(delayMultiplexer, currentTryNumber.toDouble()).toLong(),
                        TimeUnit.SECONDS
                    )
            }
    }
}