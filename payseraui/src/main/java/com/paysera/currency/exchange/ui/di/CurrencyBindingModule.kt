package com.paysera.currency.exchange.ui.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.paysera.currency.exchange.ui.CurrencyFragment
import com.paysera.currency.exchange.ui.manager.CurrencyDialogManager
import com.paysera.currency.exchange.ui.manager.CurrencyDialogManagerImpl
import com.paysera.currency.exchange.ui.manager.ErrorBannerManager
import com.paysera.currency.exchange.ui.manager.ErrorBannerManagerImpl
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
            InjectCurrentExchangeViewModel::class, InjectCurrencyDialogManager::class, InjectErrorBannerManager::class
        ]
    )
    abstract fun bindCurrencyExchangeFragment(): CurrencyFragment

    @Module
    class InjectCurrentExchangeViewModel {
        @Provides
        internal fun provideCurrencyViewModel(
            factory: ViewModelProvider.Factory,
            target: CurrencyFragment
        ) = ViewModelProviders.of(target, factory).get(CurrencyViewModel::class.java)
    }

    @Module
    class InjectCurrencyDialogManager {
        @Provides
        internal fun provideCurrencyDialogManager(currencyDialogManagerImpl: CurrencyDialogManagerImpl): CurrencyDialogManager =
            currencyDialogManagerImpl
    }

    @Module
    class InjectErrorBannerManager {
        @Provides
        internal fun provideErrorBannerManager(errorBannerManagerImpl: ErrorBannerManagerImpl): ErrorBannerManager =
            errorBannerManagerImpl
    }
}
