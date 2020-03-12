package com.paysera.currency.exchange.di.module

import com.paysera.currency.exchange.client.di.PayseraClientModule
import com.paysera.currency.exchange.common.di.NetworkModule
import com.paysera.currency.exchange.db.di.DatabaseModule
import dagger.Module

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Module(
    includes = [
        NetworkModule::class,
        PayseraClientModule::class,
        DatabaseModule::class
    ]
)
class PayseraAppModules