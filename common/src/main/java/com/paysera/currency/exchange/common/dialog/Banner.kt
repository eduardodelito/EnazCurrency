package com.paysera.currency.exchange.common.dialog

import android.os.Handler
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.paysera.currency.exchange.common.R
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by eduardo.delito on 4/13/20.
 */
object Banner {
    private const val NEW_BANNER_DELAY: Long = 500
    private var currentBannerReference: WeakReference<ErrorBannerFragment?>? = null

    /**
     * Display the banner on the given FragmentManager. If there is no banner already shown we just show the new banner. If the new banner has the
     * same hierarchy as the one already shown we suppress the new banner. If they have different hierarchy we dismiss the current one only if the
     * new one has a greater hierarchy, otherwise we suppress the new one. If the new banner is a service error banner, and right before the new
     * banner is shown there is no network connectivity, we show a network error banner instead.
     *
     * @param bannerBuilder   Banner to show
     * @param fragmentManager The FragmentManager this fragment will be added to
     * @param tag             Tag for this fragment
     */
    fun showBanner(
        mBannerBuilder: Builder,
        fragmentManager: FragmentManager,
        tag: String
    ) {
        var bannerBuilder = mBannerBuilder
        val builder =
            if (Hierarchy.SERVICE_ERROR == bannerBuilder.hierarchy && bannerBuilder.isWithNetworkError /*&& isNetworkError(
                    bannerBuilder.activity
                )*/
            ) fromNetworkError(bannerBuilder.activity)
                .also { bannerBuilder = it } else bannerBuilder
        val currentBanner = getCurrentBanner()
        if (currentBanner == null || (currentBanner.getHierarchy() != bannerBuilder.hierarchy
                    && bannerBuilder.hierarchy.ordinal > currentBanner.getHierarchy().ordinal)
        ) {
            dismissCurrentBanner()
            if (currentBanner != null) {
                // If there is already a banner showed wait until that banner disappear to show the new one.
                Handler().postDelayed({
                    showAllowingStateLoss(
                        builder,
                        fragmentManager,
                        tag
                    )
                }, NEW_BANNER_DELAY)
            } else {
                //Sometimes the activity/fragment was not created
                //and even if we show the banner it does not get shown
                Handler().post {
                    showAllowingStateLoss(
                        builder,
                        fragmentManager,
                        tag
                    )
                }
            }
        }
    }

    /**
     * Dismiss the banner from current activity
     */
    fun dismissCurrentBanner() {
        val currentBanner = getCurrentBanner()
        if (currentBanner != null && currentBanner.isAdded && currentBanner.isResumed) {
            // This call will make the ErrorBannerFragment.onDismiss() method being called at some point. So we can clear the currentBanner field
            // after that happens.
            currentBanner.dismissAllowingStateLoss()
        }
    }

    /**
     * Dismiss the banner from current activity without calling [ErrorBannerFragment.ErrorBannerListener.onErrorBannerDismiss]
     */
    fun dismissCurrentBannerSilently() {
        val currentBanner = getCurrentBanner()
        if (currentBanner != null && currentBanner.isAdded && currentBanner.isResumed) {
            currentBanner.dismissAllowingStateLossSilently()
            currentBannerReference = WeakReference(null)
        }
    }
// Implement network validation
// private fun isNetworkError(activity: FragmentActivity?): Boolean {
// }

    private fun getCurrentBanner(): ErrorBannerFragment? {
        return if (currentBannerReference != null) {
            currentBannerReference?.get()
        } else null
    }

    private fun clearBanner(tag: String?) {
        val currentBanner = getCurrentBanner()
        if (currentBanner != null && Objects.equals(tag, currentBanner.tag)) {
            currentBannerReference = WeakReference(null)
        }
    }

    private fun showAllowingStateLoss(
        builder: Builder,
        fragmentManager: FragmentManager,
        tag: String
    ) {
        currentBannerReference =
            WeakReference(builder.build())
        val currentBanner = getCurrentBanner()
        if (currentBanner != null) {
            val bannerShown =
                currentBanner.showAllowingStateLoss(fragmentManager, tag)
            if (!bannerShown) {
                clearBanner(tag)
            }
        }
    }
    /**
     * Returns a new instance of ErrorBannerBuilder
     *
     * @param message  Message to be displayed
     * @param tag      Tag for this fragment
     * @param activity The activity where the banner will be shown
     *
     * @return
     */
    /**
     * Returns a new instance of ErrorBannerBuilder to use with Aligator for showing the banner.
     *
     * @param message Message to be displayed
     * @param tag     Tag for this fragment
     *
     * @return
     */
    @JvmOverloads
    fun from(
        message: String?,
        tag: String,
        activity: FragmentActivity? = null
    ): Builder {
        return Builder(message, tag, activity)
    }

    /**
     * Returns a new instance of ErrorBannerBuilder.
     *
     * @param message  Message to be displayed
     * @param activity The activity where the banner will be shown
     *
     * @return
     */
    fun from(message: String?, activity: FragmentActivity?): Builder {
        val random = Random()
        val tag = Banner::class.java.name + random.nextInt()
        return Builder(message, tag, activity)
    }

    /**
     * Returns a new instance of ErrorBannerBuilder that represents a network error banner. The message is already set with a network connectivity
     * error text.
     *
     * @param activity The activity where the banner will be shown
     *
     * @return
     */
    private fun fromNetworkError(activity: FragmentActivity?): Builder {
        return from(activity?.getString(R.string.common_no_internet_connection), activity)
            .setHierarchy(Hierarchy.NETWORK_ERROR)
            .setBannerType(BannerType.ERROR)
    }

    class Builder(
        message: CharSequence?,
        tag: String,
        activity: FragmentActivity?
    ) {
        var title: String? = null
            private set
        val message: CharSequence? = message
        private val tag: String
        var isTransactional = false
            private set
        var isCancelable = false
            private set
        var isWithRetry = false
            private set
        var isModal = false
            private set
        var isWithNetworkError = false
            private set
        var isDisableAutoLink = false
            private set
        private val listeners: MutableList<ErrorBannerFragment.ErrorBannerListener?>
        val activity: FragmentActivity?
        var bannerType: BannerType? = null
            private set
        var hierarchy: Hierarchy
            private set
        var isAutoDismissEnabled = false
            private set
        var autoDismissDelayTime =
            AUTO_DISMISS_DEFAULT_DELAY_TIME
            private set

        fun withTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun transactional(): Builder {
            isTransactional = true
            hierarchy = Hierarchy.TRANSACTIONAL
            return this
        }

        fun cancelable(): Builder {
            isCancelable = true
            return this
        }

        fun withRetry(): Builder {
            isWithRetry = true
            hierarchy = Hierarchy.SERVICE_ERROR_RETRY
            return this
        }

        fun withNetworkError(): Builder {
            isWithNetworkError = true
            return this
        }

        fun disableAutolink(): Builder {
            isDisableAutoLink = true
            return this
        }

        fun modal(): Builder {
            isModal = true
            return this
        }

        fun addListener(listener: ErrorBannerFragment.ErrorBannerListener): Builder {
            listeners.add(listener)
            return this
        }

        fun setBannerType(bannerType: BannerType?): Builder {
            this.bannerType = bannerType
            return this
        }

        fun setHierarchy(hierarchy: Hierarchy): Builder {
            this.hierarchy = hierarchy
            return this
        }

        fun autoDismissEnabled(): Builder {
            isAutoDismissEnabled = true
            return this
        }

        /**
         * Enables auto dismiss and sets a custom delay time
         * @param autoDismissDelayTime delay time in millis
         * @return
         */
        fun autoDismissEnabled(autoDismissDelayTime: Int): Builder {
            isAutoDismissEnabled = true
            this.autoDismissDelayTime = autoDismissDelayTime
            return this
        }

        fun getListeners(): MutableList<ErrorBannerFragment.ErrorBannerListener?> {
            return listeners
        }

        fun build(): ErrorBannerFragment {
            return ErrorBannerFragment.getInstance(this)
        }

        fun show() {
            requireNotNull(activity) { "Activity is null. Use build() with Aligator instead, or provide an activity." }
            showBanner(this, activity.supportFragmentManager, tag)
        }

        companion object {
            private const val AUTO_DISMISS_DEFAULT_DELAY_TIME = 3000
        }

        init {
            listeners = arrayListOf()
            listeners.add(object : ErrorBannerFragment.ErrorBannerListener {
                override fun onErrorBannerRetry(tag: String?) {
                    clearBanner(tag)
                }

                override fun onErrorBannerDismiss(tag: String?) {
                    clearBanner(tag)
                }

            })
            this.activity = activity
            this.tag = tag
            hierarchy = Hierarchy.SERVICE_ERROR
        }
    }

    enum class BannerType(val mName: String) {
        MESSAGE("message"), WARNING("warning"), ERROR("error"), IF_APPLICABLE("if applicable");
    }

    /**
     * This enum represents the hierarchy of the Banner.
     * The Banner with the highest priority is the TRANSACTIONAL (ordinal 7), followed by
     * SERVICE_ERROR_RETRY (ordinal 6) and so on. The higher the ordinal the more important.
     * For more details about the banner behavior and design see
     * [Banner documentation](https://wiki-wdpro.disney.com/display/PROJ/Banner).
     */
    enum class Hierarchy {
        PROMOTIONAL_ALERT,
        POSITIVE_ALERT,
        VALIDATION_ALERT,
        NETWORK_ERROR,
        SERVICE_ERROR,
        SERVICE_ERROR_RETRY,
        TRANSACTIONAL
    }
}
