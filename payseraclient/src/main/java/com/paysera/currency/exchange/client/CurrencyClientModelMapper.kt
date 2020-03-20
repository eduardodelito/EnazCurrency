package com.paysera.currency.exchange.client

import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.db.entity.CurrencyEntity

/**
 * Created by eduardo.delito on 3/12/20.
 */
fun List<CurrencyRatesResult>.serviceModelToCurrencyEntity(): List<CurrencyEntity> {
    return this.map {
        CurrencyEntity(
            id = 0,
            currency = it.currency,
            currencyValue = it.currencyValue,
            currencyBalance = it.currencyBalance,
            maxConversion = it.maxConversion,
            transactionCount = it.transactionCount
        )
    }
}

fun CurrencyRatesResult.serviceModelToCurrency(): CurrencyEntity {
    return CurrencyEntity(
            id = 0,
            currency = currency,
            currencyValue = currencyValue,
            currencyBalance = currencyBalance,
            maxConversion = maxConversion,
            transactionCount = transactionCount
        )
}
