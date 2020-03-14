package com.paysera.currency.exchange.client.di

import android.content.Context
import com.paysera.currency.exchange.client.PayseraApiClient
import com.paysera.currency.exchange.client.repository.CurrencyRepository
import com.paysera.currency.exchange.client.repository.CurrencyRepositoryImpl
import com.paysera.currency.exchange.db.dao.CurrencyDao
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Module
class PayseraClientModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext() = context

    @Provides
    @Singleton
    fun provideApiClient(client: OkHttpClient.Builder) = PayseraApiClient(client)

    @Provides
    @Singleton
    fun provideCurrencyRepository(apiClient: PayseraApiClient, currencyDao: CurrencyDao): CurrencyRepository = CurrencyRepositoryImpl(apiClient, currencyDao)
}