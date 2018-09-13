package com.arudanovsky.exchange.view

import com.arudanovsky.exchange.domain.interactor.currency.CurrenciesInteractor
import com.arudanovsky.exchange.domain.model.CurrencyItem
import com.arudanovsky.exchange.domain.interactor.currency.CurrenciesInteractorImpl
import com.arudanovsky.exchange.utils.EUR_KEY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class MainPresenter constructor(private val view: MainView) {

    private var currencies: List<CurrencyItem> = emptyList()
    private var currencyKey = EUR_KEY
    private var currencyKeySubj = BehaviorSubject.createDefault(currencyKey)

    private var currenciesInteractor: CurrenciesInteractor = CurrenciesInteractorImpl()

    fun onInit() {
        Observable.just(0)
            .flatMap {
                currenciesInteractor
                .getRates(currencyKey)
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
        val newList = arrayListOf(currencies[pos]).plus(currencies.filter { it != currencies[pos] })
        currencies = newList
        view.updateList(currencies)
    }
}