package com.arudanovsky.exchange.view.base

import android.support.v4.app.Fragment
import android.widget.Toast

abstract class BaseView<T: IBasePresenter>: Fragment(), IBaseView {
    abstract var presenter: T

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}