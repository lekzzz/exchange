package com.arudanovsky.exchange

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    var items = emptyList<String>()
        set(value) {
            val oldItems = field
            field = value
            val diffCallback = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition] == value[newItemPosition]

                override fun getOldListSize() = oldItems.size

                override fun getNewListSize() = value.size

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition] == value[newItemPosition]
            })
            diffCallback.dispatchUpdatesTo(this)
        }

    val clickSubject = BehaviorSubject.create<Int>()
    val editableCurrenySubject = BehaviorSubject.create<Pair<String, BigDecimal>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder.adapterPosition)
        val watcher = Watcher((items[holder.adapterPosition]))
        holder.etValue.addTextChangedListener(watcher)
        holder.etValue.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) clickSubject.onNext(holder.adapterPosition)
            watcher.active = hasFocus
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
        private val rate: BigDecimal = Math.random().toBigDecimal()

        init {
            editableCurrenySubject
                .subscribe {
                    if (it.first != tvTitle.text)
                        etValue.text = it.second.multiply(rate).setScale(2, RoundingMode.HALF_UP).toString()
                }
        }

        fun bind(position: Int) {
            Log.d("bind", "rate = $rate")
            tvTitle.text = items[position]
        }
    }
}

fun CharSequence?.toBigDecimal() =
    if (isNullOrBlank()) BigDecimal.ZERO else BigDecimal(toString())