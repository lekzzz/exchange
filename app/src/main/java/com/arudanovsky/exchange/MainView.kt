package com.arudanovsky.exchange

interface MainView {
    fun updateList(currencies: List<CurrencyItem>)
    fun updateRates(rates: List<CurrencyItem>)
}