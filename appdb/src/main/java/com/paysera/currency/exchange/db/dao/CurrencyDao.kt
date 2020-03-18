package com.paysera.currency.exchange.db.dao

import androidx.room.*
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import io.reactivex.Observable

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Dao
interface CurrencyDao {
    @Query("SELECT * FROM CurrencyEntity")
    fun getAllCurrencies(): Observable<List<CurrencyEntity>>

    @Query("SELECT * FROM CurrencyEntity")
    fun getAllSaveCurrencies(): List<CurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencies(currencyList: List<CurrencyEntity>)

    @Query("UPDATE CurrencyEntity SET currencyBalance=:amount, isAvailable = :isAvailable WHERE currency = :currency")
    fun updateCurrencyEntity(currency: String?, amount: String?, isAvailable: Boolean)

    @Query("DELETE FROM CurrencyEntity")
    fun deleteCurrencies()
}
