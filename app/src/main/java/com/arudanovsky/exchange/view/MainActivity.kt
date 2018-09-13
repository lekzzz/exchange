package com.arudanovsky.exchange.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.arudanovsky.exchange.domain.CurrencyItem
import com.arudanovsky.exchange.R

class MainActivity : AppCompatActivity(), MainView {

    lateinit var recyclerView: RecyclerView
    lateinit var presenter: MainPresenter

    private var adapter = CurrencyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //bind
        recyclerView = findViewById(R.id.rvCurrencies)

        //create presenter
        presenter = MainPresenter(this)

        //configuration
        configureList()

        presenter.onInit()
    }

    private fun configureList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.clickSubject.hide()
            .subscribe { presenter.positionClicked(it) }
    }

    override fun updateList(currencies: List<CurrencyItem>) {
        adapter.items = currencies
    }

    override fun updateRates(rates: List<CurrencyItem>) {
        adapter.ratesSubject.onNext(rates)
    }
}
