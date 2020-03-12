package com.paysera.currency.exchange.client.repository

import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.client.model.PayseraResponse
import com.paysera.currency.exchange.client.serviceModelToCurrencyEntity
import com.paysera.currency.exchange.common.util.safeDispose
import com.paysera.currency.exchange.db.dao.PayseraDao
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by eduardo.delito on 3/11/20.
 */
interface PayseraRepository {

    fun getRates()

    fun getBase()

    fun getDate()
}

class PayseraRepositoryImpl(private val apiClient: PayseraApiClient, private val payseraDao: PayseraDao) : PayseraRepository {

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

