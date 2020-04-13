package com.paysera.currency.exchange.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

/**
 * Created by eduardo.delito on 3/30/20.
 */
class ProgressDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pick a style based on the num.
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomProgressBarTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.progress_dialog, container, false)
    }

    companion object {
        /**
         * Create a new instance of CurrencyDialogFragment, providing "num" as an
         * argument.
         */
        fun newInstance(): ProgressDialogFragment {
            return ProgressDialogFragment()
        }
    }
}
