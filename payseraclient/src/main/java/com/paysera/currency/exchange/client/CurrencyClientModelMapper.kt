package com.paysera.currency.exchange.client

import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.db.entity.CurrencyEntity

/**
 * Created by eduardo.delito on 3/12/20.
 */
fun List<CurrencyRatesResult>.serviceModelToCurrencyEntity() : List<CurrencyEntity> {
    return this.map {
        CurrencyEntity(
            id = 0,
            countryCode = it.countryCode,
            currencyAmount = it.currencyAmount
        )
    }
}
