package com.paysera.currency.exchange.ui.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.paysera.currency.exchange.ui.CurrencyFragment
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Module
abstract class CurrencyBindingModule {

    @ContributesAndroidInjector(
        modules = [
            InjectCurrentExchangeViewModel::class
        ]
    )
    abstract fun bindCurrencyExchangeFragment(): CurrencyFragment

    @Module
    class InjectCurrentExchangeViewModel {
        @Provides
        internal fun provideTrackListViewModel(
            factory: ViewModelProvider.Factory,
            target: CurrencyFragment
        ) = ViewModelProviders.of(target, factory).get(CurrencyViewModel::class.java)
    }
}
