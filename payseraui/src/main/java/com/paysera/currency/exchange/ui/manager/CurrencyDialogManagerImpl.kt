package com.paysera.currency.exchange.ui.manager

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.paysera.currency.exchange.common.R
import com.paysera.currency.exchange.ui.listener.CurrencyDialogListener
import javax.inject.Inject

/**
 * Created by eduardo.delito on 4/15/20.
 */
class CurrencyDialogManagerImpl @Inject constructor() : CurrencyDialogManager {

    /**
     *  Show dialog to select available currencies.
     *  @param context
     *  @param currencyDialogListener listener for currency UI labels
     *  @param currencies list of currencies
     *  @param if dialog shown from receive cta.
     */
    override fun showDialog(
        context: Context?,
        currencyDialogListener: CurrencyDialogListener?,
        currencies: MutableList<String?>,
        isReceive: Boolean
    ) {
        var selected: String? = ""
        val listItems = currencies.toTypedArray<CharSequence?>()
        val mBuilder = context?.let { AlertDialog.Builder(it) }
        mBuilder?.setTitle(context.getString(R.string.choose_currencies_title))
        mBuilder?.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            dialogInterface.apply { selected = listItems[i].toString() }
        }
        mBuilder?.setPositiveButton(R.string.ok) { dialog, which ->
            currencyDialogListener?.updateReceiveUI(selected, isReceive)
            dialog.cancel()
            which.or(-1)
        }
        // Set the neutral/cancel button click listener
        mBuilder?.setNegativeButton(R.string.cancel) { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
            which.or(-2)
        }
        val mDialog = mBuilder?.create()
        mDialog?.show()
    }
}
