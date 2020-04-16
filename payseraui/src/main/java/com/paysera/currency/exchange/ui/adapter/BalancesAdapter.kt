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

    private var list = mutableListOf<BalanceItem>()

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
    }

    /**
     * Function to update the track item list data
     *
     * @param list the new set of data
     */
    fun updateDataSet(newList: MutableList<BalanceItem>) {
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * Function to add an item into the list.
     *
     * @param balanceItem item data
     */
    fun addItemData(balanceItem: BalanceItem) {
        this.list.add(0, balanceItem)
        notifyItemInserted(0)
    }

    /**
     * Function to update an item into the list.
     *
     * @param balanceItem item data
     */
    fun updateItemData(balanceItem: BalanceItem) {
        this.list.indexOf(balanceItem)
        notifyDataSetChanged()
    }

    /**
     *
     */
    fun getBaseBalance(currency: String?): String? {
        return list.find { balance: BalanceItem -> currency.equals(balance.currency)}?.amount
    }

    inner class TrackViewHolder(val binding: ItemBalanceBinding) : RecyclerView.ViewHolder(binding.root)
}