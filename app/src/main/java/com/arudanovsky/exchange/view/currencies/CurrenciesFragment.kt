package com.arudanovsky.exchange.view.currencies

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arudanovsky.exchange.R
import com.arudanovsky.exchange.domain.model.CurrencyItem
import com.arudanovsky.exchange.view.base.BaseView

class CurrenciesFragment: BaseView<CurrenciesPresenter>(),
    CurrenciesView {

    lateinit var recyclerView: RecyclerView
    private var adapter = CurrencyAdapter()
    override var presenter: CurrenciesPresenter =
        CurrenciesPresenterImpl(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_currencies, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvCurrencies)

        configureList()

        presenter.onInit()
    }

    private fun configureList() {
        recyclerView.layoutManager = LinearLayoutManager(context)
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