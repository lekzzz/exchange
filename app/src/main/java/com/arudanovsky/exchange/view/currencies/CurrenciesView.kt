package com.arudanovsky.exchange.view.currencies

import com.arudanovsky.exchange.domain.model.CurrencyItem
import com.arudanovsky.exchange.view.base.IBasePresenter
import com.arudanovsky.exchange.view.base.IBaseView

interface CurrenciesView: IBaseView {
    fun updateList(currencies: List<CurrencyItem>)
    fun updateRates(rates: List<CurrencyItem>)
}
interface CurrenciesPresenter: IBasePresenter {
    fun positionClicked(pos: Int)
}