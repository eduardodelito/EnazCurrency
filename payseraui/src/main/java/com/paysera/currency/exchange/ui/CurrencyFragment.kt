package com.paysera.currency.exchange.ui

import android.widget.Toast
import com.paysera.currency.exchange.common.fragment.BaseFragment
import com.paysera.currency.exchange.ui.adapter.BalancesAdapter
import com.paysera.currency.exchange.ui.databinding.CurrencyFragmentBinding
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import kotlinx.android.synthetic.main.currency_fragment.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyFragment: BaseFragment<CurrencyFragmentBinding, CurrencyViewModel>() {

    @Inject
    override lateinit var viewModel: CurrencyViewModel

    override fun createLayout() = R.layout.currency_fragment

    override fun getBindingVariable() = BR.viewModel

    private lateinit var balancesAdapter: BalancesAdapter

    override fun initData() {
        with(viewModel) {
            getPayseraResponse()
            getBalances()
            defaultBalance()
        }
    }

    override fun initViews() {
        balancesAdapter = BalancesAdapter()
        with (recycler_view) {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = balancesAdapter
        }

        drop_down_image.setOnClickListener {
            showDialog(viewModel.currencies, false)
        }
        drop_down_receive_image.setOnClickListener {
            showDialog(viewModel.currencies, true)
        }
        submit_button.setOnClickListener {
            viewModel.computeInitialConvertedBalance(currency_lbl.text.toString(), currency_receive_lbl.text.toString(), sell_field.text.toString());
        }
    }

    override fun subscribeUi() {
        with (viewModel) {
            errorMessage.observe(viewLifecycleOwner, Observer { messageId ->
                Toast.makeText(context, messageId?.let { getString(it) }, Toast.LENGTH_LONG).show()
            })

            dialogMessage.observe(viewLifecycleOwner, Observer { message ->
                submitDialog(message)
            })

            balanceListResult.observe(viewLifecycleOwner, Observer { result ->
                balancesAdapter.updateDataSet(result)
            })

            balanceResult.observe(viewLifecycleOwner, Observer { result ->
                balancesAdapter.addItemData(result)
                receive_text.text = "+ ${result.amount}"
            })

            currentBalance.observe(viewLifecycleOwner, Observer { result ->
                amount_txt_lbl.text = result
                updateDefaultBalance(result)
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
        viewModel.computeConvertedBalance(currency_receive_lbl.text.toString(), sell_field.text.toString())
    }

    companion object {
        fun newInstance() =
            CurrencyFragment()
    }
}