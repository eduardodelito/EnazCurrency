package com.paysera.currency.exchange.ui.viewmodel

import com.paysera.currency.exchange.client.repository.PayseraRepository
import com.paysera.currency.exchange.common.viewmodel.BaseViewModel
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyViewModel @Inject constructor(
    private val payseraRepository: PayseraRepository) : BaseViewModel() {

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCleared() {
        super.onCleared()
    }
}