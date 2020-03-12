package com.paysera.currency.exchange

import androidx.multidex.MultiDex
import com.paysera.currency.exchange.client.di.PayseraClientModule
import com.paysera.currency.exchange.common.di.NetworkModule
import com.paysera.currency.exchange.db.di.DatabaseModule
import com.paysera.currency.exchange.di.component.DaggerPayseraAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/**
 * Created by eduardo.delito on 3/11/20.
 */
class PayseraApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerPayseraAppComponent
            .builder()
            .application(this)
            .payseraClient(PayseraClientModule())
            .database(DatabaseModule(this))
            .networkModule(NetworkModule())
            .build()
    }

}