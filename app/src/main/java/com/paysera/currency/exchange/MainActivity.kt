package com.paysera.currency.exchange

import android.os.Bundle
import android.widget.FrameLayout
import com.paysera.currency.exchange.common.util.navigate
import com.paysera.currency.exchange.ui.CurrencyExchangeFragment
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (layout_container is FrameLayout) {
            CurrencyExchangeFragment.newInstance().navigate(supportFragmentManager, R.id.layout_container, false)
        }
    }

}
