package com.paysera.currency.exchange.ui.util

import com.paysera.currency.exchange.client.model.CurrencyRatesResult
import com.paysera.currency.exchange.db.entity.BalanceEntity
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import com.paysera.currency.exchange.db.model.BalanceResult
import com.paysera.currency.exchange.ui.model.BalanceItem

/**
 * Created by eduardo.delito on 3/14/20.
 */
fun List<CurrencyRatesResult>.entityModelToCurrencyItem(): List<CurrencyEntity> {
    return this.map {
        CurrencyEntity(
            id = 0,
            currency = it.currency,
            currencyValue = it.currencyValue
        )
    }
}

fun MutableList<BalanceEntity>.entityModelToBalanceItem(): MutableList<BalanceItem> {
    return this.map {
        BalanceItem(
            currency = it.currency,
            amount = it.amount
        )
    }.toMutableList()
}
