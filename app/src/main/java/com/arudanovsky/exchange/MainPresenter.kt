package com.arudanovsky.exchange

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainPresenter constructor(private val view: MainView) {

    private var currencies: List<String> = emptyList()

    fun onInit() {
        Observable.just(listOf("aaa", "bbb", "ccc"))
            .repeatWhen {
                it.delay(1, TimeUnit.SECONDS)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (currencies.isEmpty() && it.isNotEmpty()) {
                    currencies = it
                    view.updateList(it)
                } else view.updateRate(it)
            }
    }

    fun aaa(pos: Int) {
        val newList = arrayListOf(currencies[pos])
        newList.addAll(currencies.filter { it != currencies[pos] })
        currencies = newList
        view.updateList(currencies)
    }
}