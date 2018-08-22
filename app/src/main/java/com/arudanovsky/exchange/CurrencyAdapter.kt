package com.arudanovsky.exchange

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.subjects.BehaviorSubject

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder.adapterPosition)
        holder.itemView.setOnClickListener { clickSubject.onNext(holder.adapterPosition) }
        holder.tvTitle.setOnClickListener { clickSubject.onNext(holder.adapterPosition) }
        holder.etValue.setOnClickListener { clickSubject.onNext(holder.adapterPosition) }
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val etValue: TextView = itemView.findViewById(R.id.etValue)
        private val currencyKey: String? = null
        private var pos: Int? = null

        fun bind(position: Int) {
            pos = position
            tvTitle.text = items[position]
        }
    }
}