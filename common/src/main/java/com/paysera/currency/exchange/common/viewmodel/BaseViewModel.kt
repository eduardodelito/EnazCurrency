package com.paysera.currency.exchange.common.viewmodel

import androidx.lifecycle.ViewModel

/**
 * Created by eduardo.delito on 3/11/20.
 */
abstract class BaseViewModel : ViewModel() {

    open fun onStart() {
    }

    open fun onResume() {
    }

    open fun onPause() {
    }

    open fun onStop() {
    }
}
