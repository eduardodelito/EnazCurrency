package com.paysera.currency.exchange.ui.di

import android.content.Context
import androidx.lifecycle.ViewModel
import com.paysera.currency.exchange.client.repository.CurrencyRepository
import com.paysera.currency.exchange.common.viewmodel.ViewModelKey
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Module
class CurrencyViewModelModule {

    @Provides
    @IntoMap
    @ViewModelKey(CurrencyViewModel::class)
    fun provideTrackListViewModel(applicationContext: Context,
        currencyRepository: CurrencyRepository): ViewModel = CurrencyViewModel(applicationContext, currencyRepository)

}