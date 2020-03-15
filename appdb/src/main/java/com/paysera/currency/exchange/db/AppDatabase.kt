package com.paysera.currency.exchange.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paysera.currency.exchange.db.dao.CurrencyDao
import com.paysera.currency.exchange.db.entity.BalanceEntity
import com.paysera.currency.exchange.db.entity.BaseAndDateEntity
import com.paysera.currency.exchange.db.entity.CurrencyEntity

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Database(entities = [CurrencyEntity::class, BaseAndDateEntity::class, BalanceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun payseraDao(): CurrencyDao

    companion object {
        const val DB_NAME = "DBPaysera"
    }
}
