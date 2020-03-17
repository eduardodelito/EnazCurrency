package com.paysera.currency.exchange.common.util

import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.reactivex.disposables.Disposable

/**
 * Created by eduardo.delito on 3/12/20.
 */
/**
 * Extension function that show and replace a fragment
 *
 * @param supportFragmentManager fragment manager class that is needed to start a fragment transaction
 * @param containerId the layout container where the fragment is inflated
 * @param isAddToBackStack the boolean flag to add the fragment into the back stack
 *
 * @return the attached fragment
 */
fun Fragment.navigate(supportFragmentManager: FragmentManager, containerId: Int, isAddToBackStack: Boolean) : Fragment {
    supportFragmentManager.beginTransaction().let {
        it.replace(containerId, this, this.javaClass.name)
        if (isAddToBackStack) {
            it.addToBackStack(this.javaClass.name)
        }
        it.show(this)
        it.commit()
    }
    return this
}

/**
 * Extension function to show or hide the back button
 *
 * @param shouldShow flag to set if the back button should be display or not
 */
fun ActionBar.showBackButton(shouldShow: Boolean) {
    this.setDisplayHomeAsUpEnabled(shouldShow)
    this.setDisplayShowHomeEnabled(shouldShow)
}

/**
 * Extension function to handle rxJava disposal safely
 *
 * @return boolean object to state the result of safe dispose
 */
fun Disposable?.safeDispose() = if (this != null && !isDisposed) {
    dispose()
    true
} else false
