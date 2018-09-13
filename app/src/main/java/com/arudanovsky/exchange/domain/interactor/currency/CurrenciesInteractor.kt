package com.arudanovsky.exchange.domain.interactor.currency

import com.arudanovsky.exchange.domain.model.CurrencyItem
import io.reactivex.Observable

interface CurrenciesInteractor {
    fun getRates(currencyKey: String): Observable<List<CurrencyItem>>
}