package com.paysera.currency.exchange.ui

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currency.exchange.common.dialog.Banner
import com.paysera.currency.exchange.common.dialog.ErrorBannerFragment
import com.paysera.currency.exchange.common.fragment.BaseFragment
import com.paysera.currency.exchange.ui.adapter.BalancesAdapter
import com.paysera.currency.exchange.ui.databinding.CurrencyFragmentBinding
import com.paysera.currency.exchange.ui.listener.CurrencyFragmentDialogListener
import com.paysera.currency.exchange.ui.manager.CurrencyDialogManager
import com.paysera.currency.exchange.ui.manager.ErrorBannerManager
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import kotlinx.android.synthetic.main.currency_fragment.*
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyFragment : BaseFragment<CurrencyFragmentBinding, CurrencyViewModel>(),
    CurrencyFragmentDialogListener {

    @Inject
    override lateinit var viewModel: CurrencyViewModel

    @Inject
    lateinit var currencyDialogManager: CurrencyDialogManager

    @Inject
    lateinit var errorBannerManager: ErrorBannerManager

    override fun createLayout() = R.layout.currency_fragment

    override fun getBindingVariable() = BR.viewModel

    private lateinit var balancesAdapter: BalancesAdapter

    private var currencyFragmentDialogListener: CurrencyFragmentDialogListener? = null

    private val TAG: String = CurrencyFragment::class.java.simpleName

    private var errorBannerFragment: ErrorBannerFragment? = null

    private var progressDialog: ProgressDialogFragment? = null

    override fun initData() {
        showProgressDialog()
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
            currencyDialogManager.showDialog(context, this, viewModel.currencies(), false)
        }
        drop_down_receive_image.setOnClickListener {
            currencyDialogManager.showDialog(context, this, viewModel.currencies(), true)
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
            sell_field.text.toString(),
            isInitial
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
                showProgressDialog()
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
        currencyFragmentDialogListener = this
        with(viewModel) {

            errorMessage.observe(viewLifecycleOwner, Observer { messageId ->
                errorBannerManager.showErrorBanner(messageId, context, activity, currencyFragmentDialogListener)
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
                if (!isLoading) progressDialog?.dismiss()
            })
        }
    }

    /**
     *  Update the currency label/added balance on the list after the conversion.
     */
    override fun updateReceiveUI(selected: String?, isReceive: Boolean) {
        if (isReceive) {
            currency_receive_lbl?.text = selected
            receive_text.text = getString(R.string.label_receive_amount_default)
        } else {
            currency_lbl.text = selected
        }
    }

    /**
     *  Show error banner listener
     *  @param bannerBuilder builder
     */
    override fun showErrorBanner(bannerBuilder: Banner.Builder) {
        errorBannerFragment = bannerBuilder.build()
        errorBannerFragment?.show(
            childFragmentManager,
            TAG
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CurrencyFragmentDialogListener) {
            currencyFragmentDialogListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        currencyFragmentDialogListener = null
        progressDialog?.dismiss()
        viewModel.requestCurrenciesDispose()
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

    private fun showProgressDialog() {
        progressDialog = ProgressDialogFragment.newInstance()
        activity?.supportFragmentManager?.let { progressDialog?.show(it, "progress") }
    }

    companion object {
        fun newInstance() =
            CurrencyFragment()
    }
}
