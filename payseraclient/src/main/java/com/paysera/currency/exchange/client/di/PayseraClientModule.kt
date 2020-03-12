package com.paysera.currency.exchange.client.di

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
class PayseraClientModule {

    @Provides
    @Singleton
    fun provideApiClient(client: OkHttpClient.Builder) = PayseraApiClient(client)

    @Provides
    @Singleton
    fun provideTrackRepository(apiClient: PayseraApiClient, trackDao: CurrencyDao): CurrencyRepository = CurrencyRepositoryImpl(apiClient, trackDao)
}