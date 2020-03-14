package com.paysera.currency.exchange.ui.viewmodel

import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.paysera.currency.exchange.client.repository.CurrencyRepository
import com.paysera.currency.exchange.common.viewmodel.BaseViewModel
import com.paysera.currency.exchange.ui.model.BalanceItem
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject


/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyViewModel @Inject constructor(private val applicationContext: Context,
    private val currencyRepository: CurrencyRepository) : BaseViewModel() {

    private var currencyDisposable : Disposable? = null
    public val _balancesResult = MutableLiveData<List<BalanceItem>>()
    public val _balanceResult = MutableLiveData<BalanceItem>()
    public var currencies = ArrayList<String>()
    private var balanceItemList = ArrayList<BalanceItem>()

    fun getCurrencies() {
        currencyDisposable = currencyRepository.getRates()
            .doFinally {  }
            .subscribe(
                {
                        result -> println("${result.rates}==========${result.base}=========${result.date}")
                },
                {
                        error -> println(error.message)
                })
    }

    fun jsonCurrency(): JSONObject {
        val jsonFileString = getJsonDataFromAsset(applicationContext, "currency.json")
        return JSONObject(jsonFileString.toString())
    }

    fun loadCurrencies() {
        val jsonObject = jsonCurrency();
        val jObject = JSONObject(jsonObject.getString("rates"))
        val keys: Iterator<String> = jObject.keys()
        jsonObject.getString("base").let { currencies.add(it) }
        while (keys.hasNext()) {
            val key = keys.next()
            currencies.add(key)
            balanceItemList.add(BalanceItem(key, jObject.getString(key)))
        }

    }

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}