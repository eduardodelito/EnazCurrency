package com.paysera.currency.exchange.client.repository

import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.client.model.PayseraResponse
import com.paysera.currency.exchange.client.serviceModelToCurrency
import com.paysera.currency.exchange.client.serviceModelToCurrencyEntity
import com.paysera.currency.exchange.common.util.safeDispose
import com.paysera.currency.exchange.db.dao.CurrencyDao
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

/**
 * Created by eduardo.delito on 3/11/20.
 */
interface CurrencyRepository {

    fun getPayseraResponse(): Single<PayseraResponse>

    fun deleteCurrencies()

    fun queryCurrencies(): Single<List<CurrencyEntity>>

    fun currencyList(): List<CurrencyEntity>

    fun currencies(): List<String?>

    fun insertOrUpdate(currency: CurrencyEntity)

    fun loadBase(currencyRatesResult: CurrencyRatesResult)

    fun commissionFeeLimit(): Int
}

class CurrencyRepositoryImpl(
    private val apiClient: PayseraApiClient,
    private val currencyDao: CurrencyDao
) : CurrencyRepository {

    private var queryCurrenciesDisposable: Disposable? = null
    private var deleteCurrencyDisposable: Disposable? = null
    private var insertCurrencyDisposable: Disposable? = null
    private var currencyList = mutableListOf<CurrencyRatesResult>()
    private var mCurrencies = mutableListOf<String?>()
    private var limit = 0

    /**
     * Request to the Currency Exchange Rate API.
     */
    override fun getPayseraResponse(): Single<PayseraResponse> =
        apiClient.getService().getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess {
                //Limit can get from the service pass it or save into database. ex. it?.limit
                limit = 5
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
    override fun currencies(): List<String?> = mCurrencies

    override fun commissionFeeLimit(): Int {
        return limit
    }

    /**
     * Delete currency list from the database
     */
    override fun deleteCurrencies() {
        deleteCurrencyDisposable = Completable.fromCallable {
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
    private fun currencyListResult(base: String?, rateList: Any?): List<CurrencyRatesResult> {
        val jObject = JSONObject(rateList.toString())
        val keys: Iterator<String> = jObject.keys()
        mCurrencies.clear()
        mCurrencies.add(base)
        currencyList.clear()
        // Max conversion can be modified from the service. Add default 1000.
        currencyList.add(CurrencyRatesResult(base, "1", "1000", "1000", 0))
        while (keys.hasNext()) {
            val key = keys.next()
            mCurrencies.add(key)
            currencyList.add(
                CurrencyRatesResult(
                    key,
                    jObject.getString(key),
                    "0",
                    "1000",
                    0
                )
            )
        }
        return currencyList
    }

    /**
     * Query currency list from the database.
     */
    override fun queryCurrencies(): Single<List<CurrencyEntity>> =
        currencyDao.getAllCurrencies().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * Insert or Update item from the database.
     * @param currency item CurrencyEntity
     */
    override fun insertOrUpdate(currency: CurrencyEntity) {
        insertCurrencyDisposable = Completable.fromCallable {
                currencyDao.insertOrUpdate(currency)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { insertCurrencyDisposable?.safeDispose() }
    }
}
