package com.arudanovsky.exchange

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class MainPresenter constructor(private val view: MainView) {

    private var currencies: List<CurrencyItem> = emptyList()
    private var currencyKey = "EUR"
    private var currencyKeySubj = BehaviorSubject.createDefault("EUR")

    fun onInit() {
        Observable.just(0)
            .flatMap {
                ApiClient.getRetrofitClient()
                .getRates(currencyKey)
                .map {
                    listOf(
                        CurrencyItem(
                            it.baseKey,
                            BigDecimal.ONE
                        )
                    ).plus(
                        it.rates.list.map {
                            CurrencyItem(
                                it.key,
                                it.rate
                            )
                        }
                    )
                }
            }
            .repeatWhen {
                it.delay(1, TimeUnit.SECONDS)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (currencies.isEmpty() && it.isNotEmpty()) {
                    currencies = it
                    view.updateList(it)
                } else {
                    view.updateRates(it)
                }
            }
    }

    fun positionClicked(pos: Int) {
        currencyKey = currencies[pos].key
        currencyKeySubj.onNext(currencies[pos].key)
        val newList = arrayListOf(currencies[pos])
        newList.addAll(currencies.filter { it != currencies[pos] })
        currencies = newList
        view.updateList(currencies)
    }
}