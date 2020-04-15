package com.paysera.currency.exchange.common.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.paysera.currency.exchange.common.R
import com.paysera.currency.exchange.common.anim.AnimationListenerAdapter
import com.paysera.currency.exchange.common.util.ViewUtil


class ErrorBannerFragment : DialogFragment() {

    private val EXTRA_FOCUS_DELAY = 200

    private var isTransactional: Boolean? = false
    private var title: String? = null
    private var message: CharSequence? = null
    private var type: String? = null
    private lateinit var hierarchy: Banner.Hierarchy

    private var listeners: MutableList<ErrorBannerListener>? = null
    private var withRetry: Boolean? = false
    private var retry = false
    private var disableAutoLink: Boolean? = false
    private var modal: Boolean? = false

    private var content: View? = null
    private val contentDescription: String? = null
    private var root: View? = null
    private var animationDuration = EXTRA_FOCUS_DELAY
    private var autoDismissEnabled: Boolean? = false
    private var autoDismissDelayTime: Int? = 0

    companion object {

        private const val IS_TRANSACTIONAL = "isTransactional"
        private const val WITH_RETRY = "withRetry"
        private const val DISABLE_AUTOLINK = "disableAutoLink"
        private const val BANNER_TITLE = "bannerTitle"
        private const val BANNER_MESSAGE = "bannerMessage"
        private const val MODAL = "modal"
        private const val HIERARCHY = "hierarchy"
        private const val TYPE = "type"
        private const val AUTO_DISMISS_ENABLED = "autoDismissEnabled"
        private const val AUTO_DISMISS_DELAY_TIME = "autoDismissDelayTime"

        fun getInstance(builder: Banner.Builder): ErrorBannerFragment {
            val errorBannerFragment = ErrorBannerFragment()
            errorBannerFragment.listeners = builder.getListeners()
            errorBannerFragment.isCancelable = builder.isCancelable
            val bundle = Bundle()
            bundle.putSerializable(HIERARCHY, builder.hierarchy)
            bundle.putBoolean(IS_TRANSACTIONAL, builder.isTransactional)
            bundle.putBoolean(WITH_RETRY, builder.isWithRetry)
            bundle.putBoolean(DISABLE_AUTOLINK, builder.isDisableAutoLink)
            bundle.putCharSequence(BANNER_MESSAGE, builder.message)
            bundle.putString(BANNER_TITLE, builder.title)
            bundle.putBoolean(MODAL, builder.isModal)
            bundle.putString(TYPE, builder.bannerType?.mName)
            bundle.putBoolean(AUTO_DISMISS_ENABLED, builder.isAutoDismissEnabled)
            bundle.putInt(AUTO_DISMISS_DELAY_TIME, builder.autoDismissDelayTime)
            errorBannerFragment.arguments = bundle
            return errorBannerFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            hierarchy = arguments?.getSerializable(HIERARCHY) as Banner.Hierarchy
            message = arguments?.getCharSequence(BANNER_MESSAGE)
            title = arguments?.getString(BANNER_TITLE)
            withRetry = arguments?.getBoolean(WITH_RETRY)
            disableAutoLink = arguments?.getBoolean(DISABLE_AUTOLINK)
            isTransactional = arguments?.getBoolean(IS_TRANSACTIONAL)
            modal = arguments?.getBoolean(MODAL)
            type = arguments?.getString(TYPE)
            autoDismissEnabled = arguments?.getBoolean(AUTO_DISMISS_ENABLED)
            autoDismissDelayTime = arguments?.getInt(AUTO_DISMISS_DELAY_TIME)
        }
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_error_banner, null)

        //Init UI elements
        content = root?.findViewById(R.id.banner_content)
        val retryButton = root?.findViewById<ImageButton>(R.id.refresh_banner_button)
        val dismissButton = root?.findViewById<ImageButton>(R.id.dismiss_banner_button)
        val titleText = root?.findViewById<TextView>(R.id.banner_title)
        val messageText = root?.findViewById<TextView>(R.id.banner_message)
        isTransactional = arguments?.getBoolean(IS_TRANSACTIONAL, false)
        withRetry = arguments?.getBoolean(WITH_RETRY, false)
        disableAutoLink = arguments?.getBoolean(DISABLE_AUTOLINK, false)


        var savedTitle: String? = null
        var savedMessage: CharSequence? = null

        if (savedInstanceState != null) {
            savedTitle = savedInstanceState.getString(BANNER_TITLE)
            savedMessage = savedInstanceState.getCharSequence(BANNER_MESSAGE)
        }

        if (savedTitle != null) {
            title = savedTitle
        }

        if (savedMessage != null) {
            message = savedMessage
        }

        if (title.isNullOrEmpty()) {
            titleText?.visibility = View.GONE
        } else {
            titleText?.text = title
        }

        if (disableAutoLink == true) {
            messageText?.autoLinkMask = 0
            messageText?.movementMethod = LinkMovementMethod.getInstance()
        }

        if (message == null || message?.length == 0) {
            throw RuntimeException("Banner must have a message")
        } else {
            messageText?.text = message
        }

        retryButton?.visibility = if (withRetry == false) View.GONE else View.VISIBLE

        val colorId: Int = when (hierarchy) {
            Banner.Hierarchy.PROMOTIONAL_ALERT -> R.color.banner_green
            Banner.Hierarchy.POSITIVE_ALERT -> R.color.banner_active_blue
            Banner.Hierarchy.TRANSACTIONAL -> R.color.banner_orange
            else -> R.color.banner_dark_blue
        }

        root?.setBackgroundColor(resources.getColor(colorId, activity?.theme))

        retryButton?.setOnClickListener {
            retry = true
            dismiss()
        }

        dismissButton?.setOnClickListener {
            retry = false
            dismiss()
        }

        if (autoDismissEnabled == true) {
            retryButton?.visibility = View.GONE
            dismissButton?.visibility = View.GONE
        }
        return root
    }

    private fun dismissWithAnimation(runnable: Runnable) {
        val context = context
        if (context != null) {
            val animation =
                AnimationUtils.loadAnimation(context, R.anim.slide_out)
            animation.setAnimationListener(object : AnimationListenerAdapter() {
                override fun onAnimationEnd(animation: Animation?) {
                    runnable.run()
                }
            })
            view?.startAnimation(animation)
        }
    }

    override fun dismiss() {
        dismissWithAnimation(Runnable { super@ErrorBannerFragment.dismiss() })
    }

    override fun dismissAllowingStateLoss() {
        dismissWithAnimation(Runnable { super@ErrorBannerFragment.dismissAllowingStateLoss() })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (listeners != null) {
            for (listener in listeners!!) {
                if (retry) listener.onErrorBannerRetry(tag) else listener.onErrorBannerDismiss(
                    tag
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setUpAutoDismiss()
    }

    private fun setUpAutoDismiss() {
        if (autoDismissEnabled == true) {
            autoDismissDelayTime?.toLong()?.let {
                root?.postDelayed({
                    if (isResumed) {
                        dismiss()
                    }
                }, it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(BANNER_TITLE, title)
        outState.putCharSequence(BANNER_MESSAGE, message)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        animationDuration = resources.getInteger(R.integer.error_banner_animation_duration)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            animationDuration += EXTRA_FOCUS_DELAY
        }
        setupWindow()
        view?.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.slide_in
            )
        )

    }

    private fun setupWindow() {
        val window = dialog?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params = window?.attributes

        //To show the error banner on top of the bottom tab bar if it exists.
        params?.gravity = Gravity.BOTTOM
        params?.width = ViewUtil.getScreenWidth(context)
        window?.attributes = params
        if (modal == false) {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
        }
    }


    /**
     * Show the banner by committing the fragment transaction even after an activity's state is saved. This is necessary if we want to avoid an
     * IllegalStateException when trying to commit the fragment transaction of the DialogFragment when the activity is stopped.
     * The banner may not shown if the activity was already destroyed.
     * This scenario may happen when trying to show the banner within a [android.os.Handler.postDelayed]
     *
     * @param manager FragmentManager
     * @param tag Tag
     * @return True if the banner was shown, otherwise false.
     */
    fun showAllowingStateLoss(
        manager: FragmentManager,
        tag: String?
    ): Boolean {
        if (isAdded || isVisible || activity != null && activity!!.isFinishing || manager.isDestroyed) {
            return false
        }
        val transaction = manager.beginTransaction()
        transaction.add(this, tag)
        transaction.commitAllowingStateLoss()
        return true
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!isAdded && !isVisible) {
            super.show(manager, tag)
        }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return if (!isAdded && !isVisible) {
            super.show(transaction, tag)
        } else 0

    }

    fun isTransactional(): Boolean? = isTransactional

    fun getHierarchy(): Banner.Hierarchy {
        //This is here to avoid getting NULL when this is called before the fragment is created.
        return if (arguments == null) {
            hierarchy
        } else arguments?.getSerializable(HIERARCHY) as Banner.Hierarchy
    }

    /**
     * Dismiss the banner from current activity without calling [ErrorBannerListener.onErrorBannerDismiss]
     */
    fun dismissAllowingStateLossSilently() {
        listeners?.clear()
        dismissAllowingStateLoss()
    }

    /**
     * Listener for the actions in the error banner
     */
    interface ErrorBannerListener {
        /**
         * To be called when the user press retry in the banner
         */
        fun onErrorBannerRetry(tag: String?)

        /**
         * To be called when the user closes the banner
         */
        fun onErrorBannerDismiss(tag: String?)
    }
}
