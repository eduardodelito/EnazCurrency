package com.paysera.currency.exchange.common.anim

import android.view.animation.Animation

/**
 * Created by eduardo.delito on 4/14/20.
 */
abstract class AnimationListenerAdapter : Animation.AnimationListener {
    /**
     * {@inheritDoc}
     */
    override fun onAnimationStart(animation: Animation?) {
        //NO-OP
    }

    /**
     * {@inheritDoc}
     */
    override fun onAnimationEnd(animation: Animation?) {
        //NO-OP
    }

    /**
     * {@inheritDoc}
     */
    override fun onAnimationRepeat(animation: Animation?) {
        //NO-OP
    }
}
