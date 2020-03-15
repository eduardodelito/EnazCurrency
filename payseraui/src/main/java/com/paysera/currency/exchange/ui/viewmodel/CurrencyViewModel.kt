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
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject


/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : BaseViewModel() {

    private var currenciesDisposable: Disposable? = null
    private var balancesDBDisposable: Disposable? = null
    private var baseDisposable: Disposable? = null
    private var currencyValueDisposable: Disposable? = null
    private var currencyInitialValueDisposable: Disposable? = null
    private var currencyIfExistDisposable: Disposable? = null

    var currencies = ArrayList<String?>()

    private val _errorMessage = MutableLiveData<Int?>()
    val errorMessage: LiveData<Int?> get() = _errorMessage

    private val _dialogMessage = MutableLiveData<String?>()
    val dialogMessage: LiveData<String?> get() = _dialogMessage

    private val _balanceListResult = MutableLiveData<MutableList<BalanceItem>>()
    val balanceListResult: LiveData<MutableList<BalanceItem>> get() = _balanceListResult

    private val _balanceResult = MutableLiveData<BalanceItem>()
    val balanceResult: LiveData<BalanceItem> get() = _balanceResult

    private val _currentBalance = MutableLiveData<String>()
    val currentBalance: LiveData<String> get() = _currentBalance

    private var defaultCurrentBalance: Double? = 0.0
    private var defaultCommissionFee: Double? = 0.0
    private var defaultBase: String? = ""
    private var defaultDate: String? = ""

    fun getPayseraResponse() {
        currenciesDisposable = currencyRepository.getPayseraResponse()
            .doFinally { currencies = currencyRepository.getCurrencies() }.subscribe(
                {
                    _errorMessage.postValue(R.string.success_message)
                    currenciesDisposable?.safeDispose()
                },
                {
                    _errorMessage.postValue(R.string.error_database)
                    currenciesDisposable?.safeDispose()
                }
            )
    }

    fun computeInitialConvertedBalance(baseCurrency: String?, currency: String?, amount: String?) {
        currencyInitialValueDisposable =
            currencyRepository.getCurrencyValue(currency).subscribe({ result ->
                result.let {
                    val total: Double?
                    if (!currency.equals(defaultBase)) {
                        total = result.currencyValue?.toDouble()
                            ?.let { value -> amount?.toDouble()?.times(value) }
                        var totalBal =
                            total?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP).toString()
                        _dialogMessage.postValue("$amount $baseCurrency to $totalBal $currency")
                    }
                }
                currencyInitialValueDisposable.safeDispose()
            }, {
                _dialogMessage.postValue("Unable to convert!")
                currencyInitialValueDisposable?.safeDispose()
            })
    }

//    fun convertedBalance(currency: String?, amount: String?) {
//        var isExist = false
//        currencyIfExistDisposable =
//            currencyRepository.getCurrencyBalance(currency).subscribe({ result ->
//                if (currency.equals(result.currency)) {
//                    isExist = true
//                    println("$currency===========${result.currency}")
//                }
//                currencyIfExistDisposable?.safeDispose()
//            }, {
//                _errorMessage.postValue(R.string.error_database)
//                currencyIfExistDisposable?.safeDispose()
//            })
//
//        if (!isExist) {
//            computeConvertedBalance(
//                currency,
//                amount
//            )
//        }
//    }

    fun computeConvertedBalance(currency: String?, amount: String?) {
        currencyValueDisposable =
            currencyRepository.getCurrencyValue(currency).subscribe({ result ->
                result.let {
                    val total: Double?
                    if (!currency.equals(defaultBase)) {
                        total = result.currencyValue?.toDouble()
                            ?.let { value -> amount?.toDouble()?.times(value) }

                        _currentBalance.postValue(amount?.toDouble()?.let { currBalance ->
                            defaultCurrentBalance?.minus(
                                currBalance
                            )
                        }.toString())
                        var totalBal =
                            total?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP).toString()
                        _balanceResult.postValue(BalanceItem(currency, totalBal))
                        currencyRepository.saveBalance(currency, totalBal)
                    }
                }
                currencyValueDisposable.safeDispose()
            }, {
                _errorMessage.postValue(R.string.error_database)
                currencyValueDisposable?.safeDispose()
            })
    }

    fun updateDefaultBalance(currBalance: String?) {
        currencyRepository.updateDefaultBalance(
            defaultBase,
            BigDecimal(currBalance).setScale(2, RoundingMode.HALF_UP).toString()
        )
        defaultBalance()
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

    fun defaultBalance() {
        baseDisposable = currencyRepository.getBase().subscribe(
            { result ->
                defaultBase = result.base
                defaultDate = result.date
                _currentBalance.postValue(result.currentBalance)
                defaultCurrentBalance = result.currentBalance?.toDouble()
                defaultCommissionFee = result.commissionFee?.toDouble()
                baseDisposable.safeDispose()
            },
            {
                _errorMessage.postValue(R.string.error_database)
                baseDisposable.safeDispose()
            })
    }
}
