package com.arudanovsky.exchange

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

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
    }

    override fun updateList(currencies: List<String>) {
        adapter.items = currencies
    }
}
