package com.paysera.currency.exchange.common.util

import android.os.Build

/**
 * Created by eduardo.delito on 4/14/20.
 */
object AndroidVersionUtil {
    fun isLollipopOrAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    fun isNougatOrAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }
}
