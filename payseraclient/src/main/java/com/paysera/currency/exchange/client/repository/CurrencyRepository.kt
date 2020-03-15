package com.paysera.currency.exchange.client.repository

import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.client.model.BaseAndDateResult
import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.client.model.PayseraResponse
import com.paysera.currency.exchange.client.serviceModelToBaseAndDateEntity
import com.paysera.currency.exchange.client.serviceModelToCurrencyEntity
import com.paysera.currency.exchange.common.util.safeDispose
import com.paysera.currency.exchange.db.dao.CurrencyDao
import com.paysera.currency.exchange.db.entity.BalanceEntity
import com.paysera.currency.exchange.db.entity.BaseAndDateEntity
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import com.paysera.currency.exchange.db.model.BalanceResult
import com.paysera.currency.exchange.db.util.dbModelToBalanceEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

/**
 * Created by eduardo.delito on 3/11/20.
 */
interface CurrencyRepository {

    fun getCurrencies(): Observable<PayseraResponse>

    fun saveCurrencies(rates: Any?, base: String?, date: String?)

    fun getSaveCurrencies(): Observable<List<CurrencyEntity>>

    fun saveBalance(currency: String?, amount: String?)

    fun getSaveBalances(): Observable<List<BalanceEntity>>
}

class CurrencyRepositoryImpl(
    private val apiClient: PayseraApiClient,
    private val currencyDao: CurrencyDao
) : CurrencyRepository {

    private var saveCurrencyDisposable: Disposable? = null
    private var saveBalanceDisposable: Disposable? = null

    override fun getCurrencies(): Observable<PayseraResponse> {
        return apiClient.getService().getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                saveCurrencies(it?.rates, it?.base, it?.date)
            }
    }

    override fun saveCurrencies(rates: Any?, base: String?, date: String?) {
        saveCurrencyDisposable = Observable.fromCallable {
                currencyDao.deleteCurrencies()
                rates?.let { rateList ->
                    val jObject = JSONObject(rateList.toString())
                    val keys: Iterator<String> = jObject.keys()
                    var currencyList = ArrayList<CurrencyRatesResult>()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        currencyList.add(CurrencyRatesResult(key, jObject.getString(key)))
                    }
                    currencyDao.insertCurrencies(currencyList.serviceModelToCurrencyEntity())
                }
                currencyDao.deleteBaseAndDate()
                currencyDao.insertBaseAndDate(
                    BaseAndDateResult(
                        base,
                        date
                    ).serviceModelToBaseAndDateEntity()
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { saveCurrencyDisposable?.safeDispose() }
    }

    override fun getSaveCurrencies(): Observable<List<CurrencyEntity>> {
        return currencyDao.getAllCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun saveBalance(currency: String?, amount: String?) {
        saveBalanceDisposable = Observable.fromCallable {
                currencyDao.insertBalanceEntity(
                    BalanceResult(
                        currency,
                        amount
                    ).dbModelToBalanceEntity()
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { saveBalanceDisposable?.safeDispose() }
    }

    override fun getSaveBalances(): Observable<List<BalanceEntity>> {
        return currencyDao.getAllBalances()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
