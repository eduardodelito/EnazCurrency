package com.paysera.currency.exchange.ui

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currency.exchange.common.dialog.Banner
import com.paysera.currency.exchange.common.dialog.ErrorBannerFragment
import com.paysera.currency.exchange.common.fragment.BaseFragment
import com.paysera.currency.exchange.ui.adapter.BalancesAdapter
import com.paysera.currency.exchange.ui.databinding.CurrencyFragmentBinding
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import kotlinx.android.synthetic.main.currency_fragment.*
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyFragment : BaseFragment<CurrencyFragmentBinding, CurrencyViewModel>() {

    @Inject
    override lateinit var viewModel: CurrencyViewModel

    override fun createLayout() = R.layout.currency_fragment

    override fun getBindingVariable() = BR.viewModel

    private lateinit var balancesAdapter: BalancesAdapter

    private var mListener: OnCurrencyFragmentListener? = null

    private val TAG: String = CurrencyFragment::class.java.simpleName

    private var errorBannerFragment: ErrorBannerFragment? = null

    override fun initData() {
        mListener?.showProgressBar()
        viewModel.requestCurrencies()
    }

    override fun initViews() {
        setHasOptionsMenu(true)
        balancesAdapter = BalancesAdapter()
        with(recycler_view) {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = balancesAdapter
        }

        drop_down_image.setOnClickListener {
            showDialog(viewModel.currencies(), false)
        }
        drop_down_receive_image.setOnClickListener {
            showDialog(viewModel.currencies(), true)
        }
        submit_button.setOnClickListener {
            computeConvertedBalance(true)
        }
    }

    fun computeConvertedBalance(isInitial: Boolean) {
        viewModel.computeConvertedBalance(
            currency_lbl.text.toString(),
            currency_receive_lbl.text.toString(),
            balancesAdapter.getBaseBalance(currency_lbl.text.toString()),
            sell_field.text.toString()
            , isInitial
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)

        //hide some items from this fragment (e.g. sort)
        menu.findItem(R.id.action_reset).isVisible = false
        menuInflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    /**
     * Option menu to delete data list.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_reset -> {
                mListener?.showProgressBar()
                viewModel.deleteData()
                receive_text.text = "+ 0"
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Subscribe view model via observer.
     */
    override fun subscribeUi() {
        with(viewModel) {

            errorMessage.observe(viewLifecycleOwner, Observer { messageId ->
                showErrorBanner(messageId)
            })

            isComputing.observe(viewLifecycleOwner, Observer { isComputing ->
                if (!isComputing) updateUI()
            })

            balanceListResult.observe(viewLifecycleOwner, Observer { result ->
                balancesAdapter.updateDataSet(result)
            })

            dialogMessage.observe(viewLifecycleOwner, Observer { dialogContentItem ->
                val startMessage = dialogContentItem?.startMessage?.let { getString(it) }
                showAlertDialog(
                    dialogContentItem?.title,
                    "$startMessage ${dialogContentItem?.endMessage}",
                    dialogContentItem?.tag,
                    dialogContentItem?.isDone
                )
            })

            currencyReceive.observe(viewLifecycleOwner, Observer { receive ->
                receive_text.text = "+ $receive"
            })

            isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
                if (!isLoading) mListener?.hideProgressBar()
            })
        }
    }

    /**
     * Update UI currency labels.
     */
    override fun updateReceiveUI(selected: String?, isReceive: Boolean) {
        if (isReceive) {
            currency_receive_lbl?.text = selected
            receive_text.text = getString(R.string.label_receive_amount_default)
        } else {
            currency_lbl.text = selected
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCurrencyFragmentListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
        viewModel.requestCurrenciesDispose()
    }

    companion object {
        fun newInstance() =
            CurrencyFragment()
    }

    /**
     * Interface to handle callbacks
     * */
    interface OnCurrencyFragmentListener {
        /**
         * Function to handle callback to show progress bar.
         * */
        fun showProgressBar()

        /**
         * Function to handle callback to hide progress bar.
         * */
        fun hideProgressBar()
    }

    private fun showAlertDialog(
        alertTitle: String?,
        alertMessage: String?,
        tag: String?,
        isDone: Boolean?
    ) {
        var alertDialogFragment = AlertDialogFragment.newInstance(alertTitle, alertMessage, isDone)
        alertDialogFragment.setTargetFragment(this, 0)
        activity?.supportFragmentManager?.let { alertDialogFragment.show(it, tag) }
    }

    private fun showErrorBanner(index: Int?) {
        if (errorBannerFragment?.isVisible == true) return
        val errorBannerListener: ErrorBannerFragment.ErrorBannerListener = object : ErrorBannerFragment.ErrorBannerListener {
            override fun onErrorBannerRetry(tag: String?) {
                //TODO: Do nothing
            }

            override fun onErrorBannerDismiss(tag: String?) {
                //TODO: Do nothing
            }
        }
        var message: String?

        when(index) {
            1 -> {
                message = getString(R.string.dialog_the_same_currency)
            }
            2 -> {
                message = getString(R.string.dialog_not_enough)
            }
            3 -> {
                message = getString(R.string.dialog_no_selected_amount)
            }
            4 -> {
                message = getString(R.string.dialog_empty_currency)
            }
            5 -> {
                message = getString(R.string.error_network_connection)
            }
            6 -> {
                message = getString(R.string.error_database)
            }
            else -> {
                message = getString(R.string.error_network_connection)
            }
        }

        val bannerBuilder: Banner.Builder = Banner.from(message, activity)
            .cancelable()
            .transactional()
            .modal()
            .addListener(errorBannerListener)

        errorBannerFragment = bannerBuilder.build()
        errorBannerFragment?.show(
            childFragmentManager,
            TAG
        )
    }
}
