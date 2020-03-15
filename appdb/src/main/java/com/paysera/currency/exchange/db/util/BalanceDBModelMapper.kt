package com.paysera.currency.exchange.db.util

import com.paysera.currency.exchange.db.entity.BalanceEntity
import com.paysera.currency.exchange.db.model.BalanceResult

/**
 * Created by eduardo.delito on 3/15/20.
 */
fun BalanceResult.dbModelToBalanceEntity() : BalanceEntity {
    return BalanceEntity(
        id = 0,
        currency = currency,
        amount = amount
    )
}
