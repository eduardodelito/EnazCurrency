package com.paysera.currency.exchange.di.module

import androidx.lifecycle.ViewModelProvider
import com.paysera.currency.exchange.common.viewmodel.ViewModelFactory
import com.paysera.currency.exchange.ui.di.CurrencyBindingModule
import dagger.Binds
import dagger.Module

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Module(
    includes = [
        CurrencyBindingModule::class
    ]
)
abstract class ViewModelBindingModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}