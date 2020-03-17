package com.paysera.currency.exchange.common.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.paysera.currency.exchange.common.viewmodel.BaseViewModel
import dagger.android.support.DaggerFragment
import java.lang.reflect.ParameterizedType

/**
 * Created by eduardo.delito on 3/11/20.
 */
abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : DaggerFragment() {

    private lateinit var viewDataBinding: T

    protected abstract val viewModel: V

    /**
     * Abstract function to set fragment layout
     *
     * @return the layout id
     */
    abstract fun createLayout(): Int

    /**
     * Abstract function to set binding variable
     *
     * @return the binding variable id
     */
    abstract fun getBindingVariable(): Int

    /**
     * Abstract function to initialize variables/objects
     */
    abstract fun initData()

    /**
     * Abstract function to initialize views
     */
    abstract fun initViews()

    /**
     * Abstract function to subscribe to live data of view model
     */
    abstract fun subscribeUi()

    /**
     * Function to get the data binding of the current fragment
     *
     * @return the data binding of the current fragment
     */
    fun getBinding() = viewDataBinding

    /**
     * Function to get the viewModel class
     *
     * @return the viewModel class
     */
    @Suppress("UNCHECKED_CAST")
    private fun getViewModelClass() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<V>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        performDataBinding()
        initData()
        initViews()
        subscribeUi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, createLayout(), container, false)
        return viewDataBinding.root
    }

    // Function to execeute the data binding
    private fun performDataBinding() {
        viewDataBinding.setVariable(getBindingVariable(), viewModel)
        viewDataBinding.executePendingBindings()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }
    /**
     *  Update the added balance on the list after the conversion.
     */
    abstract fun updateReceiveUI(selected: String?, isReceive: Boolean)

    /**
     *  Show dialog to select available currencies.
     *  @param currencies list of currencies
     *  @param if dialog shown from receive cta.
     */
    fun showDialog(currencies: ArrayList<String?>, isReceive: Boolean) {
        var selected: String? = ""
        val listItems = currencies.toTypedArray<CharSequence?>()
        val mBuilder = context?.let { AlertDialog.Builder(it) }
        mBuilder?.setTitle("Choose Currencies")
        mBuilder?.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            dialogInterface.apply { selected = listItems[i].toString()}
        }
        mBuilder?.setPositiveButton("Ok") {dialog, which  ->
            updateReceiveUI(selected, isReceive)
            dialog.cancel()
            which.or(-1)
        }
        // Set the neutral/cancel button click listener
        mBuilder?.setNegativeButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
            which.or(-2)
        }
        val mDialog = mBuilder?.create()
        mDialog?.show()
    }

    /**
     *  Update the remaining balance after the convertion.
     */
    abstract fun updateBalanceUI()

    /**
     *  Show the submitted dialog when converting currencies.
     */
    fun submitDialog(message: String?) {
        val mBuilder = context?.let { AlertDialog.Builder(it) }
        mBuilder?.setMessage("Are you sure you want to convert $message?")
        mBuilder?.setPositiveButton("Yes") {dialog, which  ->
            confirmationDialog(message)
            updateBalanceUI()
            dialog.cancel()
            which.or(-1)
        }
        // Set the neutral/cancel button click listener
        mBuilder?.setNegativeButton("No") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
            which.or(-2)
        }
        val mDialog = mBuilder?.create()
        mDialog?.show()
    }

    /**
     *  Show the confirmation dialog after the conversion.
     */
    private fun confirmationDialog(message: String?) {
        val mBuilder = context?.let { AlertDialog.Builder(it) }
        mBuilder?.setTitle("Currency Converted")
        mBuilder?.setMessage("You have converted  $message.")
        mBuilder?.setPositiveButton("Ok") {dialog, which  ->
            dialog.cancel()
            which.or(-1)
        }
        // Set the neutral/cancel button click listener
        mBuilder?.setNegativeButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
            which.or(-2)
        }
        val mDialog = mBuilder?.create()
        mDialog?.show()
    }
}
