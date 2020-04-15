package com.paysera.currency.exchange.ui.manager

import android.content.Context
import com.paysera.currency.exchange.ui.listener.CurrencyDialogListener

/**
 * Created by eduardo.delito on 4/15/20.
 */
interface CurrencyDialogManager {
    /**
     *  Show dialog to select available currencies.
     *  @param currencies list of currencies
     *  @param if dialog shown from receive cta.
     */
    fun showDialog(context: Context?, currencyDialogListener: CurrencyDialogListener?, currencies: MutableList<String?>, isReceive: Boolean)
}