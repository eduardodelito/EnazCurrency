package com.paysera.currency.exchange.client.repository

/**
 * Created by eduardo.delito on 3/12/20.
 */
sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}