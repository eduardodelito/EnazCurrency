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
        amount: String?,
        initial: Boolean
    ) {

        val amountBal: Double = amount?.toDoubleOrNull() ?: 0.0
        val currencyBal: Double = fromBalance?.toDouble() ?: 0.0

        if (amountBal > currencyBal || amountBal == 0.0 || fromCurrency.equals(toCurrency)) {
            _errorMessage.postValue(R.string.invalid_message)
            return
        }
        _isComputing.postValue(true)
        computeDisposable = currencyRepository.queryCurrencies()
            .doFinally { _isComputing.postValue(false) }
            .subscribe(
                { result ->

                    val baseBalance = result.find { currencyEntity: CurrencyEntity ->
                        currencyEntity.currency.equals(fromCurrency)
                    }

                    if (amountBal > baseBalance?.currencyBalance?.toDoubleOrNull() ?: 0.0) {
                        _errorMessage.postValue(R.string.invalid_message)
                    } else {

                        val totalBaseBalance =
                            amountBal.let { baseBalance?.currencyBalance?.toDouble()?.minus(it) }

                        val selectedCurrBal = result.find { currencyEntity: CurrencyEntity ->
                            currencyEntity.currency.equals(toCurrency)
                        }

                        val subTotal =
                            selectedCurrBal?.currencyValue?.toDouble()?.let {
                                amountBal.times(
                                    it
                                )
                            }

                        if (initial) {
                            _dialogMessage.postValue(
                                "From ${convertDoubleToBigDecimal(amountBal)} $fromCurrency to ${convertDoubleToBigDecimal(
                                    subTotal
                                )} $toCurrency"
                            )
                        } else {

                            // Update DB for base currency.
                            currencyRepository.updateFromCurrencyEntity(
                                fromCurrency,
                                convertDoubleToBigDecimal(totalBaseBalance), true
                            )

                            val total = subTotal?.plus(selectedCurrBal.currencyBalance?.toDouble() ?: 0.0)
                            println(total)

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
