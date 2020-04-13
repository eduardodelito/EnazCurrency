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
import com.paysera.currency.exchange.ui.model.DialogContentItem
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

    private val _dialogMessage = MutableLiveData<DialogContentItem?>()
    val dialogMessage: LiveData<DialogContentItem?> get() = _dialogMessage

    private val _balanceListResult = MutableLiveData<MutableList<BalanceItem>>()
    val balanceListResult: LiveData<MutableList<BalanceItem>> get() = _balanceListResult

    private val _currencyReceive = MutableLiveData<String>()
    val currencyReceive: LiveData<String> get() = _currencyReceive

    //Should come from the service response.
    private var commissionFee: Double = 0.7 // 0.7%
    private var isUILoaded: Boolean = false

    /**
     * Request to the Currency Exchange Rate API.
     */
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
                                list[0].maxConversion,
                                list[0].transactionCount
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

    /**
     * Request Currency Exchange Rate API.
     * Run request every 5 Seconds.
     */
    fun requestCurrencies() {
        requestCurrenciesDisposable =
            Observable.timer(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    getPayseraResponse()
                }
    }

    /**
     * Dispose running request.
     */
    fun requestCurrenciesDispose() {
        requestCurrenciesDisposable.safeDispose()
    }

    /**
     * Currency list store in the container.
     */
    fun currencies(): ArrayList<String?> {
        return currencyRepository.currencies()
    }

    /**
     * Submit conversion
     * @param fromCurrency from base currency.
     * @param toCurrency to target currency.
     * @param fromBalance from base currency amount.
     * @param toBalance to target currency amount.
     * @param initial true for confirmation, false for final conversion.
     */
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
        val maxConversion = list.find { currencyEntity: CurrencyEntity ->
            currencyEntity.currency.equals(fromCurrency)
        }?.maxConversion?.toDouble() ?: 0.0


        if (toBal > fromBal || toBal == 0.0 || fromCurrency.equals(toCurrency) || list.isEmpty() || fromBal > maxConversion) {
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

                        val subTotal = selectedCurrBal?.currencyValue?.toDouble()?.let {
                            toCurrency?.let { fromCurr ->
                                fromBaseBalance?.currencyValue?.toDouble()?.let { fromBalValue ->
                                    convert(
                                        fromCurr, toBal,
                                        it, fromBalValue
                                    )
                                }
                            }
                        }

                        var computedWithCommissionFee: Double?

                        val totalValueLimit: Int = result.map { it.transactionCount }.sum()

                        computedWithCommissionFee =
                            if (totalValueLimit >= currencyRepository.commissionFeeLimit()) {
                                val computeCommissionFeePercent = commissionFee.div(100)
                                val computeCommissionFee =
                                    subTotal?.times(computeCommissionFeePercent)
                                computeCommissionFee?.let { subTotal.minus(it) }
                            } else {
                                subTotal
                            }

                        val commissionFeeMessage =
                            if (totalValueLimit >= currencyRepository.commissionFeeLimit())
                                "Commission Fee - $commissionFee% $fromCurrency." else ""
                        val message = "$toBal $fromCurrency to $computedWithCommissionFee $toCurrency"
                        if (initial) {
                            _dialogMessage.postValue(DialogContentItem("Confirmation", R.string.are_you_sure_dialog, " $message? $commissionFeeMessage", "alertInitial", false))
                        } else {

                            // Update DB for base currency.
                            currencyRepository.insertOrUpdate(
                                CurrencyRatesResult(
                                    fromCurrency, selectedCurrBal?.currencyValue,
                                    totalFromBaseBalance.toString(),
                                    selectedCurrBal?.maxConversion,
                                    fromBaseBalance?.transactionCount ?: 0
                                ).serviceModelToCurrency()
                            )

                            if (toBaseBalance != null) {
                                val total = computedWithCommissionFee?.plus(
                                    toBaseBalance.currencyBalance?.toDouble() ?: 0.0
                                )

                                val limit = toBaseBalance.transactionCount.plus(1)

                                _currencyReceive.postValue(convertDoubleToBigDecimal(computedWithCommissionFee))
                                // Update DB for the converted currency.
                                currencyRepository.insertOrUpdate(
                                    CurrencyRatesResult(
                                        toBaseBalance.currency,
                                        selectedCurrBal?.currencyValue,
                                        total.toString(),
                                        toBaseBalance.maxConversion,
                                        limit
                                    ).serviceModelToCurrency()
                                )
                            } else {
                                _currencyReceive.postValue(convertDoubleToBigDecimal(computedWithCommissionFee))
                                currencyRepository.insertOrUpdate(
                                    CurrencyRatesResult(
                                        selectedCurrBal?.currency,
                                        selectedCurrBal?.currencyValue,
                                        computedWithCommissionFee.toString(),
                                        selectedCurrBal?.maxConversion,
                                        1
                                    ).serviceModelToCurrency()
                                )
                            }
                            _dialogMessage.postValue(DialogContentItem("Congratulations", R.string.you_have_converted, " $message. $commissionFeeMessage", "alertDone", true))
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

    /**
     * Convert amount to BigDecimal.
     * Set scale to 2 and Rounding mode.
     */
    private fun convertDoubleToBigDecimal(amount: Double?): String {
        return amount?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP).toString()
    }

    /**
     * Converter method
     * @param base from the selected currency.
     * @param fromAmount from the selected currency amount.
     * @param toCurrencyValue to the currency conversion value.
     */
    private fun convert(
        base: String,
        fromAmount: Double,
        toCurrencyValue: Double,
        fromCurrencyValue: Double
    ): Double {
        val toAmount: Double
        if (base == "EUR") {
            toAmount = fromAmount.div(fromCurrencyValue)
        } else {
            toAmount = fromAmount.times(toCurrencyValue)
        }
        return toAmount
    }

    /**
     * Update UI once list added/removed.
     * Get fresh data from db.
     */
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

    /**
     * Parse list from from db.
     */
    private fun parseBalanceList(result: List<CurrencyEntity>) {
        var list: MutableList<BalanceItem> = ArrayList()
        result.forEach { currencyEntity: CurrencyEntity ->
            list.add(
                BalanceItem(
                    currencyEntity.currency,
                    convertDoubleToBigDecimal(currencyEntity.currencyBalance?.toDouble())
                )
            )
        }

        if (list.isNotEmpty()) {
            _isLoading.postValue(false)
            isUILoaded = true
        }
        _balanceListResult.postValue(list)
    }

    /**
     * Delete store data from CurrencyEntity db table.
     */
    fun deleteData() {
        isUILoaded = false
        currencyRepository.deleteCurrencies()
    }
}