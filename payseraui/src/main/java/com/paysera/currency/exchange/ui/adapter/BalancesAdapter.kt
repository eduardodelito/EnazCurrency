package com.paysera.currency.exchange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paysera.currency.exchange.ui.R
import com.paysera.currency.exchange.ui.databinding.ItemBalanceBinding
import com.paysera.currency.exchange.ui.model.BalanceItem

/**
 * Created by eduardo.delito on 3/13/20.
 */
class BalancesAdapter: RecyclerView.Adapter<BalancesAdapter.TrackViewHolder>() {

    private var list: ArrayList<BalanceItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding: ItemBalanceBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_balance, parent, false)
        return TrackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.binding.item = list[position]
        holder.binding.executePendingBindings()
    }

    /**
     * Function to update the track item list data
     *
     * @param list the new set of data
     */
    fun updateDataSet(list: ArrayList<BalanceItem>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Function to update the track item list data
     *
     * @param list the new set of data
     */
    fun addDataSet(balanceItem: BalanceItem) {
        this.list.add(balanceItem)
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(val binding: ItemBalanceBinding) : RecyclerView.ViewHolder(binding.root)
}