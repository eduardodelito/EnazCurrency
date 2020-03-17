package com.paysera.currency.exchange.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paysera.currency.exchange.client.repository.CurrencyRepository
import com.paysera.currency.exchange.common.util.safeDispose
import com.paysera.currency.exchange.common.viewmodel.BaseViewModel
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import com.paysera.currency.exchange.ui.R
import com.paysera.currency.exchange.ui.model.BalanceItem
import io.reactivex.disposables.Disposable
import java.math.RoundingMode
import javax.inject.Inject


/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : BaseViewModel() {

    private var payseraResponseDisposable: Disposable? = null
    private var currenciesDisposable: Disposable? = null
    private var computeDisposable: Disposable? = null
    private var updateDisposable: Disposable? = null

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isComputing = MutableLiveData<Boolean>(true)
    val isComputing: LiveData<Boolean> get() = _isComputing

    private val _currencies = MutableLiveData<ArrayList<String?>>()
    val currencies: LiveData<ArrayList<String?>> get() = _currencies

    private val _errorMessage = MutableLiveData<Int?>()
    val errorMessage: LiveData<Int?> get() = _errorMessage

    private val _dialogMessage = MutableLiveData<String?>()
    val dialogMessage: LiveData<String?> get() = _dialogMessage

    private val _balanceListResult = MutableLiveData<MutableList<BalanceItem>>()
    val balanceListResult: LiveData<MutableList<BalanceItem>> get() = _balanceListResult

    private val _currencyBalance = MutableLiveData<String>()
    val currencyBalance: LiveData<String> get() = _currencyBalance

    //Should come from the service response.
    private var commissionFee: Double = 0.7 // 0.7%

    fun getPayseraResponse() {
        payseraResponseDisposable = currencyRepository.getPayseraResponse().subscribe(
            { result ->
                currencyRepository.saveCurrencies(result.rates, result.base)
                payseraResponseDisposable?.safeDispose()
            },
            {
                _errorMessage.postValue(R.string.error_network_connection)
                payseraResponseDisposable?.safeDispose()
            }
        )
    }

    fun getCurrencies() {
        currenciesDisposable = currencyRepository.queryCurrencies()
            .doFinally {
                _isLoading.postValue(false)
            }
            .subscribe({ result ->
                parseBalanceList(result, false)
                _errorMessage.postValue(R.string.currencies_message)
                currenciesDisposable?.safeDispose()
            }, {
                _errorMessage.postValue(R.string.error_database)
                _isLoading.postValue(false)
                currenciesDisposable?.safeDispose()
            })
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

        if (toBal > fromBal || toBal == 0.0 || fromCurrency.equals(toCurrency)) {
            _errorMessage.postValue(R.string.invalid_message)
            return
        }
        _isComputing.postValue(true)
        computeDisposable = currencyRepository.queryCurrencies()
            .doFinally { _isComputing.postValue(false) }
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

                        val selectedCurrBal = result.find { currencyEntity: CurrencyEntity ->
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
                            currencyRepository.updateFromCurrencyEntity(
                                fromCurrency,
                                convertDoubleToBigDecimal(totalFromBaseBalance), true
                            )

                            val total =
                                computedWithCommissionFee?.plus(
                                    selectedCurrBal?.currencyBalance?.toDouble() ?: 0.0
                                )

                            // Update DB for the converted currency.
                            currencyRepository.updateToCurrencyEntity(
                                toCurrency, convertDoubleToBigDecimal(total), true
                            )
                        }
                    }
                    computeDisposable?.safeDispose()
                },
                {
                    _errorMessage.postValue(R.string.error_database)
                    computeDisposable?.safeDispose()
                    _isComputing.postValue(false)
                })
    }

    private fun convertDoubleToBigDecimal(amount: Double?): String {
        return amount?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP).toString()
    }

    fun updateUI() {
        updateDisposable = currencyRepository.queryCurrencies()
            .subscribe({ result ->
                parseBalanceList(result, true)
                updateDisposable?.safeDispose()
            }, {
                _errorMessage.postValue(R.string.error_database)
                updateDisposable?.safeDispose()
            })
    }

    private fun parseBalanceList(result: List<CurrencyEntity>, isCurrencyLoaded: Boolean) {
        var mCurrencies = ArrayList<String?>()
        var list: MutableList<BalanceItem> = ArrayList()
        result.forEach { currencyEntity: CurrencyEntity ->
            if (currencyEntity.isBase) {
                _currencyBalance.postValue(currencyEntity.currencyBalance)
            }
            if (currencyEntity.isAvailable && !currencyEntity.isBase) list.add(
                BalanceItem(
                    currencyEntity.currency,
                    currencyEntity.currencyBalance
                )
            )
            if (!isCurrencyLoaded) mCurrencies.add(currencyEntity.currency)
        }
        if (!isCurrencyLoaded) _currencies.postValue(mCurrencies)
        _balanceListResult.postValue(list)
    }
}
