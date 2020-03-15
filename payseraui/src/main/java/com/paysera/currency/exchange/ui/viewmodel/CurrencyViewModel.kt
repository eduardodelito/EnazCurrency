package com.paysera.currency.exchange.ui.viewmodel

import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paysera.currency.exchange.client.repository.CurrencyRepository
import com.paysera.currency.exchange.common.util.safeDispose
import com.paysera.currency.exchange.common.viewmodel.BaseViewModel
import com.paysera.currency.exchange.ui.R
import com.paysera.currency.exchange.ui.model.BalanceItem
import com.paysera.currency.exchange.ui.util.entityModelToBalanceItem
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject


/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyViewModel @Inject constructor(private val applicationContext: Context,
    private val currencyRepository: CurrencyRepository) : BaseViewModel() {

    private var currenciesDisposable : Disposable? = null
    private var balancesDBDisposable : Disposable? = null

    var currencies = ArrayList<String>()

    private val _errorMessage = MutableLiveData<Int?>()
    val errorMessage: LiveData<Int?> get() = _errorMessage

    private val _balanceListResult = MutableLiveData<List<BalanceItem>>()
    val balanceListResult: LiveData<List<BalanceItem>> get() = _balanceListResult

    private val _balanceResult = MutableLiveData<BalanceItem>()
    val balanceResult: LiveData<BalanceItem> get() = _balanceResult

    fun getCurrencies() {
        currenciesDisposable = currencyRepository.getCurrencies()
            .doFinally {
                // Save the keyword to shared preference

            }
            .subscribe(
                {
                        result -> result.rates?.let {
                            rateList-> val jObject = JSONObject(rateList.toString())
                            val keys: Iterator<String> = jObject.keys()
                            while (keys.hasNext()) {
                                val key = keys.next()
                                currencies.add(key)
                            }
                        }
                    currenciesDisposable?.safeDispose()
                },
                {
                    _errorMessage.postValue(R.string.error_network_connection)
                    currenciesDisposable?.safeDispose()
                }
            )
    }

    fun updateBalanceUI(currency: String?, amount: String?) {
        _balanceResult.postValue(BalanceItem(currency, amount))
        currencyRepository.saveBalance(currency, amount)
    }

    fun getBalances() {
        balancesDBDisposable = currencyRepository.getSaveBalances()
            .subscribe(
                { result ->
                    _balanceListResult.postValue(result.entityModelToBalanceItem())
                    balancesDBDisposable?.safeDispose()
                },
                {
                    _errorMessage.postValue(R.string.error_database)
                    balancesDBDisposable?.safeDispose()
                }
            )
    }

//    fun getSaveCurrencies() {
//        balancesDBDisposable = currencyRepository.getSaveCurrencies()
//            .subscribe(
//                { result ->
//                    _balanceListResult.postValue(result.entityModelToCurrencyItem())
//                    balancesDBDisposable?.safeDispose()
//                },
//                {
//                    _errorMessage.postValue(R.string.error_database)
//                    balancesDBDisposable?.safeDispose()
//                }
//            )
//    }

//    fun jsonCurrency(): JSONObject {
//        val jsonFileString = getJsonDataFromAsset(applicationContext, "currency.json")
//        return JSONObject(jsonFileString.toString())
//    }

//    fun loadCurrencies() {
//        val jsonObject = jsonCurrency();
//        val jObject = JSONObject(jsonObject.getString("rates"))
//        val keys: Iterator<String> = jObject.keys()
//        jsonObject.getString("base").let { currencies.add(it) }
//        while (keys.hasNext()) {
//            val key = keys.next()
//            currencies.add(key)
//            balanceItemList.add(BalanceItem(key, jObject.getString(key)))
//        }
//
//    }
//
//    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
//        val jsonString: String
//        try {
//            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
//        } catch (ioException: IOException) {
//            ioException.printStackTrace()
//            return null
//        }
//        return jsonString
//    }
}