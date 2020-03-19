package com.paysera.currency.exchange.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.client.repository.CurrencyRepository
import com.paysera.currency.exchange.client.serviceModelToCurrency
import com.paysera.currency.exchange.common.util.safeDispose
import com.paysera.currency.exchange.common.viewmodel.BaseViewModel
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import com.paysera.currency.exchange.ui.R
import com.paysera.currency.exchange.ui.model.BalanceItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.math.RoundingMode
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : BaseViewModel() {

    private var payseraResponseDisposable: Disposable? = null
    private var requestCurrenciesDisposable: Disposable? = null
    private var updateDisposable: Disposable? = null
    private var computeDisposable: Disposable? = null

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isComputing = MutableLiveData<Boolean>(true)
    val isComputing: LiveData<Boolean> get() = _isComputing

    private val _errorMessage = MutableLiveData<Int?>()
    val errorMessage: LiveData<Int?> get() = _errorMessage

    private val _dialogMessage = MutableLiveData<String?>()
    val dialogMessage: LiveData<String?> get() = _dialogMessage

    private val _balanceListResult = MutableLiveData<MutableList<BalanceItem>>()
    val balanceListResult: LiveData<MutableList<BalanceItem>> get() = _balanceListResult

    private val _currencyReceive = MutableLiveData<String>()
    val currencyReceive: LiveData<String> get() = _currencyReceive

    //Should come from the service response.
    private var commissionFee: Double = 0.7 // 0.7%
    private var isUILoaded: Boolean = false

    private fun getPayseraResponse() {
        payseraResponseDisposable = currencyRepository.getPayseraResponse()
            .doFinally {
                if (!isUILoaded) {
                    updateUI()
                }
            }
            .subscribe(
                {
                    if (it?.rates.toString().isNullOrEmpty())
                        _errorMessage.postValue(R.string.error_network_connection)
                    if (!isUILoaded) {
                        var list = currencyRepository.currencyList()
                        currencyRepository.loadBase(
                            CurrencyRatesResult(
                                list[0].currency,
                                list[0].currencyValue,
                                list[0].currencyBalance,
                                list[0].isAvailable,
                                list[0].isBase
                            )
                        )
                    }
                    payseraResponseDisposable?.safeDispose()
                },
                {
                    _errorMessage.postValue(R.string.error_network_connection)
                    payseraResponseDisposable?.safeDispose()
                }
            )
    }


    fun requestCurrencies() {
        requestCurrenciesDisposable =
            Observable.timer(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    getPayseraResponse()
                }
    }

    fun requestCurrenciesDispose() {
        requestCurrenciesDisposable.safeDispose()
    }

    fun currencies(): ArrayList<String?> {
        return currencyRepository.currencies()
    }

    fun computeConvertedBalance(
        fromCurrency: String?,
        toCurrency: String?,
        fromBalance: String?,
        toBalance: String?,
        initial: Boolean
    ) {

        val fromBal: Double = fromBalance?.toDouble() ?: 0.0
        val toBal: Double = toBalance?.toDoubleOrNull() ?: 0.0

        val list = currencyRepository.currencyList()

        if (toBal > fromBal || toBal == 0.0 || fromCurrency.equals(toCurrency) || list.isEmpty()) {
            _errorMessage.postValue(R.string.invalid_message)
            return
        }
        computeDisposable = currencyRepository.queryCurrencies()
            .subscribe(
                { result ->

                    // Get the base balance to convert
                    val fromBaseBalance = result.find { currencyEntity: CurrencyEntity ->
                        currencyEntity.currency.equals(fromCurrency)
                    }

                    // Check if selected amount is greater than base balance to convert.
                    // If true, return an invalid message.
                    if (toBal > fromBaseBalance?.currencyBalance?.toDoubleOrNull() ?: 0.0) {
                        _errorMessage.postValue(R.string.invalid_message)
                    } else {

                        val totalFromBaseBalance =
                            toBal.let { fromBaseBalance?.currencyBalance?.toDouble()?.minus(it) }

                        val selectedCurrBal = list.find { currencyEntity: CurrencyEntity ->
                            currencyEntity.currency.equals(toCurrency)
                        }

                        val toBaseBalance = result.find { currencyEntity: CurrencyEntity ->
                            currencyEntity.currency.equals(toCurrency)
                        }

                        val subTotal =
                            selectedCurrBal?.currencyValue?.toDouble()?.let {
                                toBal.times(
                                    it
                                )
                            }

                        val isCommissionFeeApplied =
                            result.filter { currencyEntity: CurrencyEntity -> currencyEntity.isAvailable }.size

                        var computedWithCommissionFee: Double?

                        computedWithCommissionFee = if (isCommissionFeeApplied > 5) {
                            val computeCommissionFeePercent = commissionFee.div(100)
                            val computeCommissionFee = subTotal?.times(computeCommissionFeePercent)
                            computeCommissionFee?.let { subTotal.minus(it) }
                        } else {
                            subTotal
                        }

                        val commissionFeeMessage = if (isCommissionFeeApplied > 5)
                            "Commission Fee - $commissionFee% $fromCurrency" else ""

                        if (initial) {

                            _dialogMessage.postValue(
                                "From ${convertDoubleToBigDecimal(toBal)} $fromCurrency to ${convertDoubleToBigDecimal(
                                    computedWithCommissionFee
                                )} $toCurrency. $commissionFeeMessage"
                            )
                        } else {

                            // Update DB for base currency.
                            currencyRepository.insertOrUpdate(
                                CurrencyRatesResult(
                                    fromCurrency, selectedCurrBal?.currencyValue,
                                    convertDoubleToBigDecimal(totalFromBaseBalance), true, false
                                ).serviceModelToCurrency()
                            )

                            if (toBaseBalance != null) {
                                val total = computedWithCommissionFee?.plus(
                                    toBaseBalance.currencyBalance?.toDouble() ?: 0.0
                                )

                                _currencyReceive.postValue(convertDoubleToBigDecimal(total))
                                // Update DB for the converted currency.
                                currencyRepository.insertOrUpdate(
                                    CurrencyRatesResult(
                                        toBaseBalance.currency,
                                        selectedCurrBal?.currencyValue,
                                        convertDoubleToBigDecimal(total),
                                        true,
                                        false
                                    ).serviceModelToCurrency()
                                )
                            } else {
                                _currencyReceive.postValue(
                                    convertDoubleToBigDecimal(
                                        computedWithCommissionFee
                                    )
                                )
                                currencyRepository.insertOrUpdate(
                                    CurrencyRatesResult(
                                        selectedCurrBal?.currency,
                                        selectedCurrBal?.currencyValue,
                                        convertDoubleToBigDecimal(computedWithCommissionFee),
                                        true,
                                        false
                                    ).serviceModelToCurrency()
                                )
                            }
                            _isComputing.postValue(false)
                        }
                    }
                    computeDisposable?.safeDispose()
                },
                {
                    _errorMessage.postValue(R.string.error_database)
                    computeDisposable?.safeDispose()
                })
    }

    private fun convertDoubleToBigDecimal(amount: Double?): String {
        return amount?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP).toString()
    }

    fun updateUI() {
        updateDisposable = currencyRepository.queryCurrencies()
            .subscribe({ result ->
                parseBalanceList(result)
                updateDisposable?.safeDispose()
            }, {
                _errorMessage.postValue(R.string.error_database)
                updateDisposable?.safeDispose()
            })
    }

    private fun parseBalanceList(result: List<CurrencyEntity>) {
        var list: MutableList<BalanceItem> = ArrayList()
        result.forEach { currencyEntity: CurrencyEntity ->
            if (currencyEntity.isAvailable) list.add(
                BalanceItem(
                    currencyEntity.currency,
                    currencyEntity.currencyBalance
                )
            )
        }

        if (list.isNotEmpty()) {
            _isLoading.postValue(false)
            isUILoaded = true
        }
        _balanceListResult.postValue(list)
    }

    fun deleteData() {
        isUILoaded = false
        currencyRepository.deleteCurrencies()
    }
}