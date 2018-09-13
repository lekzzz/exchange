package com.arudanovsky.exchange.view.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<T: IBaseView>(protected val view: T): IBasePresenter {
    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    protected fun addDisposable(d: Disposable) {
        compositeDisposable.add(d)
    }
}