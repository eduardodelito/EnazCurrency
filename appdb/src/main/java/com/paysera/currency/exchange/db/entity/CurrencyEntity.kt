package com.paysera.currency.exchange.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Entity(tableName = "CurrencyEntity")
data class CurrencyEntity (

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "countryCode") var countryCode: String?,
    @ColumnInfo(name = "currencyAmount") var currencyAmount: String?
)