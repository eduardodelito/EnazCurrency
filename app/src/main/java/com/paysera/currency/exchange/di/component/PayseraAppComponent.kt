package com.paysera.currency.exchange.di.component

import android.app.Application
import com.paysera.currency.exchange.PayseraApplication
import com.paysera.currency.exchange.client.di.PayseraClientModule
import com.paysera.currency.exchange.common.di.NetworkModule
import com.paysera.currency.exchange.db.di.DatabaseModule
import com.paysera.currency.exchange.di.module.ActivityBindingModule
import com.paysera.currency.exchange.di.module.PayseraAppModules
import com.paysera.currency.exchange.di.module.ViewModelBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * Created by eduardo.delito on 3/12/20.
 */
@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ActivityBindingModule::class,
    ViewModelBindingModule::class,
    PayseraAppModules::class]
)

interface PayseraAppComponent : AndroidInjector<PayseraApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun payseraClient(payseraClientModule: PayseraClientModule): Builder
        fun database(databaseModule: DatabaseModule): Builder
        fun networkModule(networkModule: NetworkModule): Builder
        fun build(): PayseraAppComponent
    }
}
