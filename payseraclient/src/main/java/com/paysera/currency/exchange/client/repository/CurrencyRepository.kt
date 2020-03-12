package com.paysera.currency.exchange.client.repository

import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.client.model.PayseraResponse
import com.paysera.currency.exchange.client.serviceModelToCurrencyEntity
import com.paysera.currency.exchange.common.util.safeDispose
import com.paysera.currency.exchange.db.dao.CurrencyDao
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by eduardo.delito on 3/11/20.
 */
interface CurrencyRepository {

    fun getRates(): Observable<PayseraResponse>

    fun saveCurrencies(currencies: List<CurrencyEntity>?)

    fun getBase()

    fun getDate()
}

class CurrencyRepositoryImpl(private val apiClient: PayseraApiClient, private val currencyDao: CurrencyDao) : CurrencyRepository {

    private var saveCurrencyDisposable : Disposable? = null

    override fun getRates() : Observable<PayseraResponse> {
        return apiClient.getService().getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                saveCurrencies(it?.results?.serviceModelToCurrencyEntity())
            }
    }

    override fun getBase() {
        TODO("Not yet implemented")
    }

    override fun getDate() {
        TODO("Not yet implemented")
    }

    override fun saveCurrencies(currencies: List<CurrencyEntity>?) {
        saveCurrencyDisposable = Observable.fromCallable {
            currencies?.let { println(it) } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { saveCurrencyDisposable?.safeDispose() }
    }
}

