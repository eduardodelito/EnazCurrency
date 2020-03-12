package com.paysera.currency.exchange.ui.di

import androidx.lifecycle.ViewModel
import com.paysera.currency.exchange.client.repository.PayseraRepository
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
    fun provideTrackListViewModel(
        payseraRepository: PayseraRepository): ViewModel = CurrencyViewModel(payseraRepository)

}