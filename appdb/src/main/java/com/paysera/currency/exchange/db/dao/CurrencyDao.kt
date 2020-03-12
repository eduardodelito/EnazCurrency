package com.paysera.currency.exchange.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import io.reactivex.Observable

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Dao
interface CurrencyDao {
    @Query("SELECT * FROM CurrencyEntity")
    fun getAllCurrencies(): Observable<List<CurrencyEntity>>
}