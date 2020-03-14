package com.paysera.currency.exchange.client.repository

import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.client.model.PayseraResponse
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

    fun saveCurrencies(rates: Any?, base: String?, date: String?)

    fun insertCurrencies(currencyEntity: ArrayList<CurrencyEntity>)
}

class CurrencyRepositoryImpl(private val apiClient: PayseraApiClient, private val currencyDao: CurrencyDao) : CurrencyRepository {

    private var saveCurrencyDisposable : Disposable? = null

    override fun getRates() : Observable<PayseraResponse> {
        return apiClient.getService().getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                saveCurrencies(it?.rates, it?.base, it?.date)
            }
    }

    override fun saveCurrencies(rates: Any?, base: String?, date: String?) {
        saveCurrencyDisposable = Observable.fromCallable {
                rates?.let { rateList-> println("rateList     $rateList") }
                base?.let { base-> println("base      $base") }
                date?.let { date-> println("date       $date") }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { saveCurrencyDisposable?.safeDispose() }
    }

    override fun insertCurrencies(currencyEntity: ArrayList<CurrencyEntity>) {
        currencyDao.insertCurrencies(currencyEntity)
    }
}

