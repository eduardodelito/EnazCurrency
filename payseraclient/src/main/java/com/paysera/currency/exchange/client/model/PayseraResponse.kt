package com.paysera.currency.exchange.client.model

/**
 * Created by eduardo.delito on 3/11/20.
 */
data class PayseraResponse(val rates: Any?, val base: String?, val date: String?)

data class CurrencyRatesResult (val countryCode : String?, val currencyAmount : String?)