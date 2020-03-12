package com.paysera.currency.exchange.di.component

import android.app.Application
import com.paysera.currency.exchange.PayseraApplication
import com.paysera.currency.exchange.common.di.NetworkModule
import com.paysera.currency.exchange.di.module.ActivityBindingModule
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
    AndroidInjectionModule::class]
)

interface PayseraAppComponent : AndroidInjector<PayseraApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): PayseraAppComponent
    }
}