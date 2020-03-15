package com.paysera.currency.exchange.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by eduardo.delito on 3/15/20.
 */
@Entity(tableName = "BalanceEntity")
data class BalanceEntity (

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "currency") var currency: String?,
    @ColumnInfo(name = "amount") var amount: String?
)
