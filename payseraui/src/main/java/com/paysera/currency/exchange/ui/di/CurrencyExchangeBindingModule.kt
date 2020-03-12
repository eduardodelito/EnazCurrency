package com.paysera.currency.exchange.ui.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.paysera.currency.exchange.ui.CurrencyExchangeFragment
import com.paysera.currency.exchange.ui.viewmodel.CurrentExchangeViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Module
abstract class CurrencyExchangeBindingModule {

    @ContributesAndroidInjector(
        modules = [
            InjectCurrentExchangeViewModel::class
        ]
    )
    abstract fun bindCurrencyExchangeFragment(): CurrencyExchangeFragment

    @Module
    class InjectCurrentExchangeViewModel {
        @Provides
        internal fun provideTrackListViewModel(
            factory: ViewModelProvider.Factory,
            target: CurrencyExchangeFragment
        ) = ViewModelProviders.of(target, factory).get(CurrentExchangeViewModel::class.java)
    }
}
