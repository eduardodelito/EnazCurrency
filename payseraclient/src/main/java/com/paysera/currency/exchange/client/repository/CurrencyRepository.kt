package com.paysera.currency.exchange.client.repository

import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.client.model.PayseraResponse
import com.paysera.currency.exchange.client.serviceModelToCurrency
import com.paysera.currency.exchange.client.serviceModelToCurrencyEntity
import com.paysera.currency.exchange.common.util.safeDispose
import com.paysera.currency.exchange.db.dao.CurrencyDao
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

/**
 * Created by eduardo.delito on 3/11/20.
 */
interface CurrencyRepository {

    fun getPayseraResponse(): Observable<PayseraResponse>

    fun deleteCurrencies()

    fun queryCurrencies(): Observable<List<CurrencyEntity>>

    fun currencyList(): List<CurrencyEntity>

    fun currencies(): ArrayList<String?>

    fun insertOrUpdate(currency: CurrencyEntity)

    fun loadBase(currencyRatesResult: CurrencyRatesResult)
}

class CurrencyRepositoryImpl(
    private val apiClient: PayseraApiClient,
    private val currencyDao: CurrencyDao
) : CurrencyRepository {

    private var queryCurrenciesDisposable: Disposable? = null
    private var deleteCurrencyDisposable: Disposable? = null
    private var insertCurrencyDisposable: Disposable? = null
    private var currencyList = ArrayList<CurrencyRatesResult>()
    private var mCurrencies = ArrayList<String?>()

    /**
     * Request to the Currency Exchange Rate API.
     */
    override fun getPayseraResponse(): Observable<PayseraResponse> =
        apiClient.getService().getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                currencyListResult(it?.base, it?.rates)
            }

    /**
     * Store base currency into CurrencyEntity table.
     */
    override fun loadBase(currencyRatesResult: CurrencyRatesResult) {
        queryCurrenciesDisposable = queryCurrencies()
            .subscribe({ result ->
                if (result.isEmpty()) {
                    insertOrUpdate(currencyRatesResult.serviceModelToCurrency())
                }
                queryCurrenciesDisposable?.safeDispose()
            }, {
                queryCurrenciesDisposable?.safeDispose()
            })
    }

    /**
     * CurrencyEntity list store in the container.
     */
    override fun currencyList(): List<CurrencyEntity> = currencyList.serviceModelToCurrencyEntity()

    /**
     * Currency list store in the container.
     */
    override fun currencies(): ArrayList<String?> = mCurrencies

    /**
     * Delete currency list from the database
     */
    override fun deleteCurrencies() {
        deleteCurrencyDisposable = Observable.fromCallable {
                currencyDao.deleteCurrencies()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { deleteCurrencyDisposable?.safeDispose() }
    }

    /**
     * Parse currencies response from API.
     * @param base currency EUR.
     * @param rateList currencies and rates.
     */
    private fun currencyListResult(base: String?, rateList: Any?): ArrayList<CurrencyRatesResult> {
        val jObject = JSONObject(rateList.toString())
        val keys: Iterator<String> = jObject.keys()
        mCurrencies.clear()
        mCurrencies.add(base)
        currencyList.clear()
        currencyList.add(CurrencyRatesResult(base, "1", "1000"))
        while (keys.hasNext()) {
            val key = keys.next()
            mCurrencies.add(key)
            currencyList.add(
                CurrencyRatesResult(
                    key,
                    jObject.getString(key),
                    "0"
                )
            )
        }
        return currencyList
    }

    /**
     * Query currency list from the database.
     */
    override fun queryCurrencies(): Observable<List<CurrencyEntity>> =
        currencyDao.getAllCurrencies().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * Insert or Update item from the database.
     * @param currency item CurrencyEntity
     */
    override fun insertOrUpdate(currency: CurrencyEntity) {
        insertCurrencyDisposable = Observable.fromCallable {
                currencyDao.insertOrUpdate(currency)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { insertCurrencyDisposable?.safeDispose() }
    }
}
