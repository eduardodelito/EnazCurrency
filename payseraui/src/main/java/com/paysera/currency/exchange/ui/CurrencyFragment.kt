package com.paysera.currency.exchange.ui

import com.paysera.currency.exchange.common.fragment.BaseFragment
import com.paysera.currency.exchange.ui.adapter.BalancesAdapter
import com.paysera.currency.exchange.ui.databinding.CurrencyFragmentBinding
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import kotlinx.android.synthetic.main.currency_fragment.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currency.exchange.ui.model.BalanceItem
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
        viewModel.getCurrencies()
        viewModel.loadCurrencies()
    }

    override fun initViews() {
        balancesAdapter = BalancesAdapter()
        with (recycler_view) {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = balancesAdapter
        }

        drop_down_image.setOnClickListener {
            showDialog(currency_lbl, viewModel.currencies)
        }
        drop_down_receive_image.setOnClickListener {
            showDialog(currency_receive_lbl, viewModel.currencies)
        }
        submit_button.setOnClickListener {
            submitDialog(currency_receive_lbl.text.toString(), sell_field.text.toString())
        }
    }

    override fun subscribeUi() {
        with (viewModel) {
            _balanceResult.observe(viewLifecycleOwner, Observer { result ->
                balancesAdapter.addDataSet(result)
            })
        }
    }

    override fun updateBalanceUI(currency: String?, amount: String?) {
        viewModel._balanceResult.postValue(BalanceItem(currency, amount))
        receive_text.text = "+ $amount"
    }

    override fun message(convertedAmount: String?): String {
        return "You have converted ${sell_field.text} ${currency_lbl.text} to " +
                "$convertedAmount ${currency_receive_lbl.text}. Commission Fee - 0.70"
    }

    companion object {
        fun newInstance() =
            CurrencyFragment()
    }
}