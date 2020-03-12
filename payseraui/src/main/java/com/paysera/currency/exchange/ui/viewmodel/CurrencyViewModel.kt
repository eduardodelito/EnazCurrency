package com.paysera.currency.exchange.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.paysera.currency.exchange.client.repository.CurrencyRepository
import com.paysera.currency.exchange.client.serviceModelToCurrencyEntity
import com.paysera.currency.exchange.common.viewmodel.BaseViewModel
import com.paysera.currency.exchange.ui.model.CurrencyItem
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository) : BaseViewModel() {

    private var currencyDisposable : Disposable? = null
    private val _currencyListResult = MutableLiveData<List<CurrencyItem>>()

    fun getCurrencies() {
        currencyDisposable = currencyRepository.getRates()
            .doFinally {  }
            .subscribe(
                {
                        result -> println(result.results)
                },
                {
                        error -> println(error.message)
                })
    }

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