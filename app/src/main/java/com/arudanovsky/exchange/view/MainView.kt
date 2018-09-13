package com.arudanovsky.exchange.view

import com.arudanovsky.exchange.domain.model.CurrencyItem

interface MainView {
    fun updateList(currencies: List<CurrencyItem>)
    fun updateRates(rates: List<CurrencyItem>)
}