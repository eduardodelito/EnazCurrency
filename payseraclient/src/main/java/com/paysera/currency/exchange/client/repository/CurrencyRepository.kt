package com.paysera.currency.exchange.client.repository

import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.db.dao.CurrencyDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by eduardo.delito on 3/11/20.
 */
interface CurrencyRepository {

    fun getRates()

    fun getBase()

    fun getDate()
}

class CurrencyRepositoryImpl(private val apiClient: PayseraApiClient, private val currencyDao: CurrencyDao) : CurrencyRepository {

    private var saveCurrencyDisposable : Disposable? = null

    override fun getRates() {
        saveCurrencyDisposable = apiClient.getService().getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {result -> println("${result.results.size}")},
                {error -> println(error.message + "Cannot be found")}
            )
    }

    override fun getBase() {
        TODO("Not yet implemented")
    }

    override fun getDate() {
        TODO("Not yet implemented")
    }

}

