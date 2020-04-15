package com.paysera.currency.exchange.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

/**
 * Created by eduardo.delito on 3/30/20.
 */
class AlertDialogFragment : DialogFragment() {

    private var alertTitle: String? = null
    private var content: String? = null
    private var isDone: Boolean? = null
    private var callback: CurrencyFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alertTitle = arguments?.getString(ALERT_TITLE)
        content = arguments?.getString(CONTENT)
        isDone = arguments?.getBoolean(IS_DONE)

        try {
            callback = targetFragment as CurrencyFragment?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement Callback interface")
        }

        // Pick a style based on the num.
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomProgressBarTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.alert_dialog, container, false)

        val alertTitleView = view.findViewById<View>(R.id.alert_title) as TextView
        alertTitleView.text = alertTitle

        val dialogMessageView = view.findViewById<View>(R.id.alert_message) as TextView
        dialogMessageView.text = content

        val dialogCancelBtn = view.findViewById<View>(R.id.cancel_btn) as TextView
        dialogCancelBtn.setOnClickListener { dismiss() }

        val dialogOkBtn = view.findViewById<View>(R.id.ok_btn) as TextView
        dialogOkBtn.setOnClickListener {
            if (isDone == false) {
                callback?.computeConvertedBalance(false)
            }
            dismiss()
        }
        return view
    }

    companion object {

        const val ALERT_TITLE = "alertTitle"
        const val CONTENT = "content"
        const val IS_DONE = "isDOne"

        /**
         * Create a new instance of CurrencyDialogFragment, providing "num" as an
         * argument.
         */
        fun newInstance(
            alertTitle: String?,
            content: String?,
            isDone: Boolean?
        ): AlertDialogFragment {
            val alertDialogFragment = AlertDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            args.putString(ALERT_TITLE, alertTitle)
            args.putString(CONTENT, content)
            isDone?.let { args.putBoolean(IS_DONE, it) }
            alertDialogFragment.arguments = args

            return alertDialogFragment
        }
    }
}
