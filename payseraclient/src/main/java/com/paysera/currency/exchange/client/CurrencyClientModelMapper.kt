package com.paysera.currency.exchange.client

import com.paysera.currency.exchange.client.model.BaseAndDateResult
import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.db.entity.BaseAndDateEntity
import com.paysera.currency.exchange.db.entity.CurrencyEntity

/**
 * Created by eduardo.delito on 3/12/20.
 */
fun List<CurrencyRatesResult>.serviceModelToCurrencyEntity() : List<CurrencyEntity> {
    return this.map {
        CurrencyEntity(
            id = 0,
            currency = it.currency,
            currencyValue = it.currencyValue
        )
    }
}

fun BaseAndDateResult.serviceModelToBaseAndDateEntity() : BaseAndDateEntity {
    return BaseAndDateEntity(id = 0, base = base, date = date)
}
