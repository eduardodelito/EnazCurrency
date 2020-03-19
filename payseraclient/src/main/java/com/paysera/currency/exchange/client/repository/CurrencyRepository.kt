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
import java.util.concurrent.TimeUnit

/**
 * Created by eduardo.delito on 3/11/20.
 */
interface CurrencyRepository {

    fun getPayseraResponse(): Observable<PayseraResponse>

    fun saveCurrencies(rates: Any?, base: String?)

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
    private var saveCurrencyDisposable: Disposable? = null
    private var deleteCurrencyDisposable: Disposable? = null
    private var updateFromCurrencyDisposable: Disposable? = null
    private var updateToCurrencyDisposable: Disposable? = null
    private var insertCurrencyDisposable: Disposable? = null
    private var currencyList = ArrayList<CurrencyRatesResult>()
    private var mCurrencies = ArrayList<String?>()

    override fun getPayseraResponse(): Observable<PayseraResponse> =
        apiClient.getService().getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                currencyListResult(it?.base, it?.rates)
            }

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

    override fun currencyList(): List<CurrencyEntity> = currencyList.serviceModelToCurrencyEntity()

    override fun currencies(): ArrayList<String?> = mCurrencies

    override fun saveCurrencies(rates: Any?, base: String?) {
        saveCurrencyDisposable = Observable.fromCallable {
                rates?.let { rateList ->
                    currencyDao.insertCurrencies(
                        currencyListResult(
                            base,
                            rateList
                        ).serviceModelToCurrencyEntity()
                    )
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { saveCurrencyDisposable?.safeDispose() }
    }

    override fun deleteCurrencies() {
        deleteCurrencyDisposable = Observable.fromCallable {
                currencyDao.deleteCurrencies()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { deleteCurrencyDisposable?.safeDispose() }
    }

    private fun currencyListResult(base: String?, rateList: Any?): ArrayList<CurrencyRatesResult> {
        val jObject = JSONObject(rateList.toString())
        val keys: Iterator<String> = jObject.keys()
        mCurrencies.clear()
        mCurrencies.add(base)
        currencyList.clear()
        currencyList.add(CurrencyRatesResult(base, "1", "1000", true, true))
        while (keys.hasNext()) {
            val key = keys.next()
            mCurrencies.add(key)
            currencyList.add(
                CurrencyRatesResult(
                    key,
                    jObject.getString(key),
                    "0",
                    false,
                    false
                )
            )
        }
        return currencyList
    }

    override fun queryCurrencies(): Observable<List<CurrencyEntity>> =
        currencyDao.getAllCurrencies().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun insertOrUpdate(currency: CurrencyEntity) {
        insertCurrencyDisposable = Observable.fromCallable {
                currencyDao.insertOrUpdate(currency)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { insertCurrencyDisposable?.safeDispose() }
    }
}
