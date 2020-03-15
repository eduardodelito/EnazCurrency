package com.paysera.currency.exchange.db.dao

import androidx.room.*
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

    @Query("SELECT * FROM CurrencyEntity WHERE currency = :currency LIMIT 1")
    fun findCurrencyEntity(currency: String?): Observable<CurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencies(currencyList: List<CurrencyEntity>)

    @Query("DELETE FROM CurrencyEntity")
    fun deleteCurrencies()

    @Query("SELECT * FROM BaseAndDateEntity")
    fun getBaseAndDateEntity(): Observable<BaseAndDateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBaseAndDate(baseAndDateEntity: BaseAndDateEntity)

    @Query("UPDATE BaseAndDateEntity SET currentBalance=:currentBalance WHERE base = :base")
    fun updateBaseAndDate(base: String?, currentBalance: String?)

    @Query("DELETE FROM BaseAndDateEntity")
    fun deleteBaseAndDate()

    @Query("SELECT * FROM BalanceEntity")
    fun getAllBalances(): Observable<MutableList<BalanceEntity>>

    @Query("SELECT * FROM BalanceEntity WHERE currency = :currency LIMIT 1")
    fun findBalanceEntity(currency: String?): Observable<BalanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBalanceEntity(balanceEntity: BalanceEntity)

    @Query("DELETE FROM BalanceEntity")
    fun deleteBalanceEntity()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(balanceEntity: BalanceEntity) : Long

    @Update
    fun update(balanceEntity: BalanceEntity)

    @Transaction
    fun insertOrUpdate(balanceEntity: BalanceEntity) {
        if (insertIgnore(balanceEntity) == -1L) {
            update(balanceEntity)
        }
    }
}
