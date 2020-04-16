package com.paysera.currency.exchange.ui.manager

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.paysera.currency.exchange.common.dialog.Banner
import com.paysera.currency.exchange.common.dialog.ErrorBannerFragment
import com.paysera.currency.exchange.ui.R
import com.paysera.currency.exchange.ui.listener.CurrencyFragmentDialogListener
import javax.inject.Inject

/**
 * Created by eduardo.delito on 4/16/20.
 */
interface ErrorBannerManager {
    /**
     *  Show Error Banner
     *  @param index type of error message.
     */
    fun showErrorBanner(
        index: Int?,
        context: Context?,
        activity: FragmentActivity?,
        currencyDialogListener: CurrencyFragmentDialogListener?)
}

class ErrorBannerManagerImpl @Inject constructor() : ErrorBannerManager {

    /**
     *  Show Error Banner
     *  @param index type of error message.
     */
    override fun showErrorBanner(
        index: Int?,
        context: Context?,
        activity: FragmentActivity?,
        currencyDialogListener: CurrencyFragmentDialogListener?) {
        val errorBannerListener: ErrorBannerFragment.ErrorBannerListener =
            object : ErrorBannerFragment.ErrorBannerListener {
                override fun onErrorBannerRetry(tag: String?) {
                    //TODO: Do nothing
                }

                override fun onErrorBannerDismiss(tag: String?) {
                    //TODO: Do nothing
                }
            }

        var message = when (index) {
            1 -> context?.getString(R.string.dialog_the_same_currency)
            2 -> context?.getString(R.string.dialog_not_enough)
            3 -> context?.getString(R.string.dialog_no_selected_amount)
            4 -> context?.getString(R.string.dialog_empty_currency)
            5 -> context?.getString(R.string.error_network_connection)
            6 -> context?.getString(R.string.error_database)
            else -> context?.getString(R.string.error_network_connection)
        }

        val bannerBuilder: Banner.Builder = Banner.from(message, activity)
            .cancelable()
            .transactional()
            .modal()
            .addListener(errorBannerListener)

        currencyDialogListener?.showErrorBanner(bannerBuilder)
    }
}
