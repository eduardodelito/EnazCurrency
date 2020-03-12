package com.paysera.currency.exchange.di.module

import com.paysera.currency.exchange.MainActivity
import com.paysera.currency.exchange.ui.di.CurrencyBindingModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(
        modules = [
            CurrencyBindingModule::class
        ]
    )
    abstract fun bindMainActivity(): MainActivity
}
