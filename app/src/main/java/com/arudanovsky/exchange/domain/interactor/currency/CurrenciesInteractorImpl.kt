package com.arudanovsky.exchange.domain.interactor.currency

import com.arudanovsky.exchange.domain.model.CurrencyItem
import com.arudanovsky.exchange.domain.interactor.BaseInteractor
import io.reactivex.Observable
import java.math.BigDecimal

class CurrenciesInteractorImpl: BaseInteractor(), CurrenciesInteractor {

    override fun getRates(currencyKey: String): Observable<List<CurrencyItem>> {
        return client.getRates(currencyKey)
            .map {
                listOf(
                    CurrencyItem(
                        it.baseKey,
                        BigDecimal.ONE
                    )
                ).plus(
                    it.rates.map {
                        CurrencyItem(
                            it.key,
                            it.value
                        )
                    }
                )
            }
    }
}