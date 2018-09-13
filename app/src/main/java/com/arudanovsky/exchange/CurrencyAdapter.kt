package com.arudanovsky.exchange

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
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
        //todo use item
        holder.bind(holder.adapterPosition)
        val watcher = Watcher(items[holder.adapterPosition].key)

        holder.etValue.addTextChangedListener(watcher)
        holder.etValue.setOnFocusChangeListener { _, hasFocus ->
            watcher.active = hasFocus
            if (hasFocus) {
                editableCurrenySubject.onNext(
                    Pair(
                        items[holder.adapterPosition].key,
                        holder.etValue.text.toBigDecimal()
                    )
                )
                clickSubject.onNext(holder.adapterPosition)
            }
        }

        if (items[holder.adapterPosition].key == editableCurrenySubject.value?.first) {
            holder.etValue.text = editableCurrenySubject.value?.second.toString()
        } else {
            holder.etValue.text = holder.calculateValue(
                editableCurrenySubject.value?.second ?: BigDecimal.ZERO,
                ratesSubject.value?.firstOrNull{ it.key == items[holder.adapterPosition].key }?.rate ?: BigDecimal.ONE
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
        private var rate: BigDecimal = BigDecimal.ONE

        init {
            Observable.combineLatest(
                editableCurrenySubject,
                ratesSubject,
                BiFunction<Pair<String, BigDecimal>, List<CurrencyItem>, Triple<String, BigDecimal, BigDecimal>> { t1, t2 ->
                    val rate = if (t1.first == t2.firstOrNull()?.key) {
                        t2.firstOrNull{ it.key == currencyKey }?.rate ?: BigDecimal.ONE
                    } else BigDecimal.ONE
                    Triple(t1.first, t1.second, rate)
                }
            )
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.first != tvTitle.text && it.third != BigDecimal.ONE) {
                        etValue.text = calculateValue(it.second, it.third)
                    }
                }
        }

        //todo to the utils
        fun calculateValue(value: BigDecimal, rate: BigDecimal) = String.format(
            Locale.getDefault(),
            value
                .multiply(rate)
                .setScale(2, RoundingMode.HALF_UP).toString(),
            null
        )

        fun bind(position: Int) {
            currencyKey = items[position].key
            rate = items[position].rate
            tvTitle.text = currencyKey
        }
    }
}

fun CharSequence?.toBigDecimal() =
    if (isNullOrBlank()) BigDecimal.ZERO else BigDecimal(toString())