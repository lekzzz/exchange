package com.arudanovsky.exchange

interface MainView {
    fun updateList(currencies: List<String>)
    fun updateRate(rates: List<String>)
}