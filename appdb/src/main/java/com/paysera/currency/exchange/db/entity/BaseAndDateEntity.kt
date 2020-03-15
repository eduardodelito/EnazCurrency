package com.paysera.currency.exchange.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by eduardo.delito on 3/14/20.
 */
@Entity(tableName = "BaseAndDateEntity")
data class BaseAndDateEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "base") var base: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "currentBalance") var currentBalance: String?,
    @ColumnInfo(name = "commissionFee") var commissionFee: String?
)