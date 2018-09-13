package com.arudanovsky.exchange.view

import com.arudanovsky.exchange.domain.CurrencyItem

interface MainView {
    fun updateList(currencies: List<CurrencyItem>)
    fun updateRates(rates: List<CurrencyItem>)
}