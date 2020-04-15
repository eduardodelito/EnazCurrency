package com.paysera.currency.exchange.ui.listener

/**
 * Created by eduardo.delito on 4/15/20.
 */
interface CurrencyDialogListener {
    /**
    * Update UI currency labels.
    */
    fun updateReceiveUI(selected: String?, isReceive: Boolean)
}