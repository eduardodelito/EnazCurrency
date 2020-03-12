package com.paysera.currency.exchange.ui

import com.paysera.currency.exchange.common.fragment.BaseFragment
import com.paysera.currency.exchange.ui.databinding.CurrencyFragmentBinding
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyFragment: BaseFragment<CurrencyFragmentBinding, CurrencyViewModel>() {

    @Inject
    override lateinit var viewModel: CurrencyViewModel

    override fun createLayout() = R.layout.currency_fragment

    override fun getBindingVariable() = BR.viewModel

    override fun initData() {}

    override fun initViews() {

    }

    override fun subscribeUi() {
        viewModel.getCurrencies()
    }

    companion object {
        fun newInstance() =
            CurrencyFragment()
    }
}