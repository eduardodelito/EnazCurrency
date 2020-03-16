package com.paysera.currency.exchange.client.repository

import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.client.model.PayseraResponse
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

    fun saveCurrencies(rates: Any?, base: String?)

    fun queryCurrencies(): Observable<List<CurrencyEntity>>

    fun updateCurrencyEntity(currency: String?, amount: String?, isAvailable: Boolean)

}

class CurrencyRepositoryImpl(
    private val apiClient: PayseraApiClient,
    private val currencyDao: CurrencyDao
) : CurrencyRepository {

    private var saveCurrencyDisposable: Disposable? = null
    private var updateCurrencyDisposable: Disposable? = null

    override fun getPayseraResponse(): Observable<PayseraResponse> {
        return apiClient.getService().getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    override fun saveCurrencies(rates: Any?, base: String?) {
        saveCurrencyDisposable = Observable.fromCallable {
                currencyDao.deleteCurrencies()
                rates?.let { rateList ->
                    currencyDao.insertCurrencies(
                        currencyListResult(
                            base,
                            rateList
                        ).serviceModelToCurrencyEntity()
                    )
                }
                saveCurrencyDisposable.safeDispose()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { saveCurrencyDisposable?.safeDispose() }
    }

    private fun currencyListResult(base: String?, rateList: Any?): ArrayList<CurrencyRatesResult> {
        val jObject = JSONObject(rateList.toString())
        val keys: Iterator<String> = jObject.keys()
        var currencyList = ArrayList<CurrencyRatesResult>()
        currencyList.add(CurrencyRatesResult(base, "1", "1000", false, true))
        while (keys.hasNext()) {
            val key = keys.next()
            currencyList.add(
                CurrencyRatesResult(
                    key,
                    jObject.getString(key),
                    "",
                    false,
                    false
                )
            )
        }
        return currencyList
    }

    override fun updateCurrencyEntity(currency: String?, amount: String?, isAvailable: Boolean) {
        updateCurrencyDisposable = Observable.fromCallable {
                currencyDao.updateCurrencyEntity(currency, amount, isAvailable)
                updateCurrencyDisposable.safeDispose()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { updateCurrencyDisposable?.safeDispose() }
    }

    override fun queryCurrencies(): Observable<List<CurrencyEntity>> {
        return currencyDao.getAllCurrencies().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
