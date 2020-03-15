package com.paysera.currency.exchange.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paysera.currency.exchange.db.entity.BalanceEntity
import com.paysera.currency.exchange.db.entity.BaseAndDateEntity
import com.paysera.currency.exchange.db.entity.CurrencyEntity
import io.reactivex.Observable

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Dao
interface CurrencyDao {
    @Query("SELECT * FROM CurrencyEntity")
    fun getAllCurrencies(): Observable<List<CurrencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencies(currencyList: List<CurrencyEntity>)

    @Query("DELETE FROM CurrencyEntity")
    fun deleteCurrencies()

    @Query("SELECT * FROM BaseAndDateEntity")
    fun getBaseAndDateEntity(): BaseAndDateEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBaseAndDate(baseAndDateEntity: BaseAndDateEntity)

    @Query("DELETE FROM BaseAndDateEntity")
    fun deleteBaseAndDate()

    @Query("SELECT * FROM BalanceEntity")
    fun getAllBalances(): Observable<List<BalanceEntity>>

    @Query("SELECT * FROM BalanceEntity")
    fun getBalanceEntity(): BalanceEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBalanceEntity(balanceEntity: BalanceEntity)

    @Query("DELETE FROM BalanceEntity")
    fun deleteBalanceEntity()
}
