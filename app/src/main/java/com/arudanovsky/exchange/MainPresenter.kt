package com.arudanovsky.exchange

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainPresenter constructor(private val view: MainView) {

    fun onInit() {
        Observable.just(emptyList<String>())
            .repeatWhen {
                it.delay(1, TimeUnit.SECONDS)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.updateList(it)
            }
    }
}