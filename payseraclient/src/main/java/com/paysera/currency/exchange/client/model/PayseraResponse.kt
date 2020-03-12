package com.paysera.currency.exchange.client.model

/**
 * Created by eduardo.delito on 3/11/20.
 */
data class PayseraResponse(val results: ArrayList<CurrencyRatesResult>)

data class CurrencyRatesResult (val countryCode : String?, val currencyAmount : String?)