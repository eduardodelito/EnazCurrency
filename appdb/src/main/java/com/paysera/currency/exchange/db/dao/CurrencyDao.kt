package com.paysera.currency.exchange.db.dao

import androidx.room.*
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import io.reactivex.Single

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Dao
interface CurrencyDao {
    @Query("SELECT * FROM CurrencyEntity")
    fun getAllCurrencies(): Single<List<CurrencyEntity>>

    @Query("DELETE FROM CurrencyEntity")
    fun deleteCurrencies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currencyEntity: CurrencyEntity)

    @Query("SELECT * from CurrencyEntity WHERE currency= :currency")
    fun getCurrencyEntityByCurrency(currency: String?): List<CurrencyEntity>

    @Query("UPDATE CurrencyEntity SET currencyBalance=:amount, maxConversion=:maxConversion, transactionCount=:transactionCount WHERE currency = :currency")
    fun updateQuantity(
        currency: String?,
        amount: String?,
        maxConversion: String?,
        transactionCount: Int
    )

    @Transaction
    fun insertOrUpdate(currency: CurrencyEntity) {
        val currencyFromDB = getCurrencyEntityByCurrency(currency.currency)

        if (currencyFromDB.isEmpty()) insert(currency)
        else updateQuantity(
            currency.currency,
            currency.currencyBalance,
            currency.maxConversion,
            currency.transactionCount
        )
    }
}
