package com.paysera.currency.exchange.ui

import android.widget.Toast
import com.paysera.currency.exchange.common.fragment.BaseFragment
import com.paysera.currency.exchange.ui.adapter.BalancesAdapter
import com.paysera.currency.exchange.ui.databinding.CurrencyFragmentBinding
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import kotlinx.android.synthetic.main.currency_fragment.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currency.exchange.common.util.CustomProgressBar
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyFragment : BaseFragment<CurrencyFragmentBinding, CurrencyViewModel>() {

    val progressBar = CustomProgressBar()

    @Inject
    override lateinit var viewModel: CurrencyViewModel

    override fun createLayout() = R.layout.currency_fragment

    override fun getBindingVariable() = BR.viewModel

    private lateinit var balancesAdapter: BalancesAdapter
    private var mCurrencies = ArrayList<String?>()

    override fun initData() {
        with(viewModel) {
            getPayseraResponse()
            getCurrencies()
        }
    }

    override fun initViews() {
        balancesAdapter = BalancesAdapter()
        with(recycler_view) {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = balancesAdapter
        }

        context?.let { progressBar.show(it, "Please wait...") }

        drop_down_image.setOnClickListener {
            showDialog(mCurrencies, false)
        }
        drop_down_receive_image.setOnClickListener {
            showDialog(mCurrencies, true)
        }
        submit_button.setOnClickListener {
            viewModel.computeConvertedBalance(
                currency_lbl.text.toString(),
                currency_receive_lbl.text.toString(),
                amount_txt_lbl.text.toString(),
                sell_field.text.toString()
            )
        }
    }

    override fun subscribeUi() {
        with(viewModel) {

            errorMessage.observe(viewLifecycleOwner, Observer { messageId ->
                Toast.makeText(context, messageId?.let { getString(it) }, Toast.LENGTH_LONG).show()
            })

            isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
                if (isLoading) progressBar.dialog.show() else progressBar.dialog.hide()
            })

            isComputing.observe(viewLifecycleOwner, Observer { isComputing ->
                if (isComputing) progressBar.dialog.show()
                else {
                    progressBar.dialog.hide()
                    updateUI()
                }
            })

            isUpdating.observe(viewLifecycleOwner, Observer { isUpdating ->
                if (isUpdating) progressBar.dialog.show() else progressBar.dialog.hide()
            })

            currencies.observe(viewLifecycleOwner, Observer { result ->
                mCurrencies = result
            })

            currencyBalance.observe(viewLifecycleOwner, Observer { result ->
                amount_txt_lbl.text = result
            })

            balanceListResult.observe(viewLifecycleOwner, Observer { result ->
                balancesAdapter.updateDataSet(result)
            })
        }
    }

    override fun updateReceiveUI(selected: String?, isReceive: Boolean) {
        if (isReceive) {
            currency_receive_lbl?.text = selected
            receive_text.text = "+ 0"
        } else {
            currency_lbl.text = selected
        }
    }

    override fun updateBalanceUI() {
//        viewModel.computeConvertedBalance(currency_receive_lbl.text.toString(), sell_field.text.toString())
    }

    companion object {
        fun newInstance() =
            CurrencyFragment()
    }
}
