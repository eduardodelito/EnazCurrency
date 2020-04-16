package com.paysera.currency.exchange

import android.os.Bundle
import com.paysera.currency.exchange.common.util.navigate
import com.paysera.currency.exchange.ui.CurrencyFragment
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CurrencyFragment.newInstance().navigate(supportFragmentManager, R.id.layout_container, false)
    }
}
