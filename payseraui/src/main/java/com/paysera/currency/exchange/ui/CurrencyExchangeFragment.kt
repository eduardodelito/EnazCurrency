package com.paysera.currency.exchange.ui

import com.paysera.currency.exchange.common.fragment.BaseFragment
import com.paysera.currency.exchange.ui.databinding.CurrentExchangeFragmentBinding
import com.paysera.currency.exchange.ui.viewmodel.CurrentExchangeViewModel
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyExchangeFragment: BaseFragment<CurrentExchangeFragmentBinding, CurrentExchangeViewModel>() {

    @Inject
    override lateinit var viewModel: CurrentExchangeViewModel

    override fun createLayout() = R.layout.current_exchange_fragment

    override fun getBindingVariable() = BR.viewModel

    override fun initData() {}

    override fun initViews() {
        TODO("Not yet implemented")
    }

    override fun subscribeUi() {
        TODO("Not yet implemented")
    }

    companion object {
        fun newInstance() =
            CurrencyExchangeFragment()
    }
}