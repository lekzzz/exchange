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
        holder.bind(holder.adapterPosition)
        val watcher = Watcher(items[holder.adapterPosition].key)
        holder.etValue.addTextChangedListener(watcher)
        holder.etValue.setOnFocusChangeListener { v, hasFocus ->
            watcher.active = hasFocus
            if (hasFocus && items[holder.adapterPosition].key != editableCurrenySubject.value?.first) {
                clickSubject.onNext(holder.adapterPosition)
                editableCurrenySubject.onNext(Pair(items[holder.adapterPosition].key, holder.etValue.text.toBigDecimal()))
            }
        }
        holder.itemView.setOnClickListener { holder.etValue.requestFocus() }
        holder.tvTitle.setOnClickListener { holder.etValue.requestFocus() }
    }

    inner class Watcher(val currencyKey: String): TextWatcher {
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

        init {
            Observable.combineLatest(
                editableCurrenySubject,
                ratesSubject,
                BiFunction<Pair<String, BigDecimal>, List<CurrencyItem>, Triple<String, BigDecimal, BigDecimal>> { t1, t2 ->
                    Triple(t1.first, t1.second, t2.firstOrNull{ it.key == tvTitle.text }?.rate ?: BigDecimal.ONE)
                }
            )
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    if (it.first != tvTitle.text && it.third != BigDecimal.ONE) {
                        etValue.text = it.second
                            .multiply(it.third)
                            .setScale(2, RoundingMode.HALF_UP)
                            .toString()
                    }
                }
        }

        fun bind(position: Int) {
            tvTitle.text = items[position].key
        }
    }
}

fun CharSequence?.toBigDecimal() =
    if (isNullOrBlank()) BigDecimal.ZERO else BigDecimal(toString())