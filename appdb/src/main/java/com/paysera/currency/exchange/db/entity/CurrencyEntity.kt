package com.paysera.currency.exchange.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Entity(tableName = "CurrencyEntity")
data class CurrencyEntity (

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "currency") var currency: String?,
    @ColumnInfo(name = "currencyValue") var currencyValue: String?,
    @ColumnInfo(name = "currencyBalance") var currencyBalance: String?
)
