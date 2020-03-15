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

    fun getCurrencies(): ArrayList<String?>

    fun getPayseraResponse(): Observable<PayseraResponse>

    fun saveCurrencies(rates: Any?, base: String?, date: String?)

    fun getSaveCurrencies(): Observable<List<CurrencyEntity>>

    fun saveBalance(currency: String?, amount: String?)

    fun getSaveBalances(): Observable<MutableList<BalanceEntity>>

    fun getBase(): Observable<BaseAndDateEntity>

    fun getCurrencyValue(currency: String?): Observable<CurrencyEntity>

    fun getCurrencyBalance(currency: String?): Observable<BalanceEntity>

    fun updateDefaultBalance(base: String?, currentBalance: String?)
}

class CurrencyRepositoryImpl(
    private val apiClient: PayseraApiClient,
    private val currencyDao: CurrencyDao
) : CurrencyRepository {

    private var saveCurrencyDisposable: Disposable? = null
    private var saveBalanceDisposable: Disposable? = null
    private var saveDefaultBalanceDisposable: Disposable? = null
    private var currencies = ArrayList<String?>()

    override fun getCurrencies(): ArrayList<String?> {
        return currencies;
    }

    override fun getPayseraResponse(): Observable<PayseraResponse> {
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
                    currencies.add(base)
                    while (keys.hasNext()) {
                        val key = keys.next()
                        currencies.add(key)
                        currencyList.add(CurrencyRatesResult(key, jObject.getString(key)))
                    }
                    currencyDao.insertCurrencies(currencyList.serviceModelToCurrencyEntity())
                }
                currencyDao.insertBaseAndDate(
                    BaseAndDateResult(
                        base,
                        date,
                        "1000", // Should be in the service response
                        "0.7" // Should be in the service response
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

    override fun getSaveBalances(): Observable<MutableList<BalanceEntity>> {
        return currencyDao.getAllBalances()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getBase(): Observable<BaseAndDateEntity> {
        return currencyDao.getBaseAndDateEntity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCurrencyValue(currency: String?): Observable<CurrencyEntity> {
        return currencyDao.findCurrencyEntity(currency).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     *  Load base for the current balance
     *  @param base EUR value
     *  @param currentBalance default balance
     */
    override fun updateDefaultBalance(base: String?, currentBalance: String?) {
        saveDefaultBalanceDisposable = Observable.fromCallable {
                currencyDao.updateBaseAndDate(base, currentBalance)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { saveDefaultBalanceDisposable?.safeDispose() }
    }

    override fun getCurrencyBalance(currency: String?): Observable<BalanceEntity> {
        return currencyDao.findBalanceEntity(currency).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
