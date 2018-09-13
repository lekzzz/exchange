package com.arudanovsky.exchange.view.currencies

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arudanovsky.exchange.domain.model.CurrencyItem
import com.arudanovsky.exchange.R
import com.arudanovsky.exchange.utils.formatMultipliedDecimals
import com.arudanovsky.exchange.utils.toBigDecimal
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    var items = emptyList<CurrencyItem>()
        set(value) {
            val oldItems = field
            field = value
            val diffCallback = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition].key == value[newItemPosition].key

                override fun getOldListSize() = oldItems.size

                override fun getNewListSize() = value.size

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition].rate.compareTo(value[newItemPosition].rate) == 0
            })
            diffCallback.dispatchUpdatesTo(this)
        }

    val clickSubject = BehaviorSubject.create<Int>()
    val editableCurrenySubject = BehaviorSubject.create<Pair<String, BigDecimal>>()
    val ratesSubject = BehaviorSubject.create<List<CurrencyItem>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = items[holder.adapterPosition]
        holder.bind(currency)
        val watcher = Watcher(currency.key)

        holder.etValue.addTextChangedListener(watcher)
        holder.etValue.setOnFocusChangeListener { _, hasFocus ->
            watcher.active = hasFocus
            if (hasFocus) {
                editableCurrenySubject.onNext(
                    Pair(
                        currency.key,
                        holder.etValue.text.toBigDecimal()
                    )
                )
                clickSubject.onNext(holder.adapterPosition)
            }
        }

        if (currency.key == editableCurrenySubject.value?.first) {
            holder.etValue.text = editableCurrenySubject.value?.second.toString()
        } else {
            holder.etValue.text = formatMultipliedDecimals(
                editableCurrenySubject.value?.second ?: BigDecimal.ZERO,
                ratesSubject.value?.firstOrNull { it.key == currency.key }?.rate ?: BigDecimal.ONE
            )
        }

        holder.itemView.setOnClickListener { holder.etValue.requestFocus() }
        holder.tvTitle.setOnClickListener { holder.etValue.requestFocus() }
    }

    inner class Watcher(private val currencyKey: String): TextWatcher {
        var active = false
        override fun afterTextChanged(s: Editable?) {
            if (active) editableCurrenySubject.onNext(Pair(currencyKey, s.toBigDecimal()))
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val etValue: TextView = itemView.findViewById(R.id.etValue)
        private var currencyKey: String? = null

        init {
            Observable.combineLatest(
                editableCurrenySubject,
                ratesSubject,
                BiFunction<Pair<String, BigDecimal>, List<CurrencyItem>, Triple<String, BigDecimal, BigDecimal>> { pair, listOfRates ->
                    val rate = if (pair.first == listOfRates.firstOrNull()?.key) {
                        listOfRates.firstOrNull{ it.key == currencyKey }?.rate ?: BigDecimal.ONE
                    } else BigDecimal.ONE
                    Triple(pair.first, pair.second, rate)
                }
            )
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.first != tvTitle.text && it.third != BigDecimal.ONE) {
                        etValue.text = formatMultipliedDecimals(
                            it.second,
                            it.third
                        )
                    }
                }
        }

        fun bind(currency: CurrencyItem) {
            currencyKey = currency.key
            tvTitle.text = currencyKey
        }
    }
}