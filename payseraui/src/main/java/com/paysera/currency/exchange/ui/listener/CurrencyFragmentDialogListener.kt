package com.paysera.currency.exchange.ui.listener

import com.paysera.currency.exchange.common.dialog.Banner

/**
 * Created by eduardo.delito on 4/15/20.
 */
interface CurrencyFragmentDialogListener {
    /**
    * Update UI currency labels.
    */
    fun updateReceiveUI(selected: String?, isReceive: Boolean)

    /**
     *  Show error banner listener
     *  @param bannerBuilder builder
     */
    fun showErrorBanner(bannerBuilder: Banner.Builder)
}
