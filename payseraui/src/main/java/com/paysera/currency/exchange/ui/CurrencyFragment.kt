package com.paysera.currency.exchange.ui

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.paysera.currency.exchange.common.fragment.BaseFragment
import com.paysera.currency.exchange.ui.adapter.BalancesAdapter
import com.paysera.currency.exchange.ui.databinding.CurrencyFragmentBinding
import com.paysera.currency.exchange.ui.viewmodel.CurrencyViewModel
import kotlinx.android.synthetic.main.currency_fragment.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyFragment : BaseFragment<CurrencyFragmentBinding, CurrencyViewModel>() {

    @Inject
    override lateinit var viewModel: CurrencyViewModel

    override fun createLayout() = R.layout.currency_fragment

    override fun getBindingVariable() = BR.viewModel

    private lateinit var balancesAdapter: BalancesAdapter

    private var mListener: OnCurrencyFragmentListener? = null

    override fun initData() {
        mListener?.showProgressBar()
        viewModel.requestCurrencies()
    }

    override fun initViews() {
        setHasOptionsMenu(true)
        balancesAdapter = BalancesAdapter()
        with(recycler_view) {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = balancesAdapter
        }

        drop_down_image.setOnClickListener {
            showDialog(viewModel.currencies(), false)
        }
        drop_down_receive_image.setOnClickListener {
            showDialog(viewModel.currencies(), true)
        }
        submit_button.setOnClickListener {
            viewModel.computeConvertedBalance(
                currency_lbl.text.toString(),
                currency_receive_lbl.text.toString(),
                balancesAdapter.getBaseBalance(currency_lbl.text.toString()),
                sell_field.text.toString()
                , true
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)

        //hide some items from this fragment (e.g. sort)
        menu.findItem(R.id.action_reset).isVisible = false
        menuInflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    /**
     * Option menu to delete data list.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_reset -> {
                mListener?.showProgressBar()
                viewModel.deleteData()
                receive_text.text = "+ 0"
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Subscribe view model via observer.
     */
    override fun subscribeUi() {
        with(viewModel) {

            errorMessage.observe(viewLifecycleOwner, Observer { messageId ->
                Toast.makeText(context, messageId?.let { getString(it) }, Toast.LENGTH_LONG).show()
            })

            isComputing.observe(viewLifecycleOwner, Observer { isComputing ->
                if (!isComputing) updateUI()
            })

            balanceListResult.observe(viewLifecycleOwner, Observer { result ->
                balancesAdapter.updateDataSet(result)
            })

            dialogMessage.observe(viewLifecycleOwner, Observer { message ->
                submitDialog(message)
            })

            currencyReceive.observe(viewLifecycleOwner, Observer { receive ->
                receive_text.text = "+ $receive"
            })

            isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
                if (!isLoading) mListener?.hideProgressBar()
            })
        }
    }

    /**
     * Update UI currency labels.
     */
    override fun updateReceiveUI(selected: String?, isReceive: Boolean) {
        if (isReceive) {
            currency_receive_lbl?.text = selected
            receive_text.text = getString(R.string.label_receive_amount_default)
        } else {
            currency_lbl.text = selected
        }
    }

    /**
     * Update UI after dialog confirmation and continue the conversion.
     */
    override fun updateBalanceUI() {
        viewModel.computeConvertedBalance(
            currency_lbl.text.toString(),
            currency_receive_lbl.text.toString(),
            balancesAdapter.getBaseBalance(currency_lbl.text.toString()),
            sell_field.text.toString()
            , false
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCurrencyFragmentListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
        viewModel.requestCurrenciesDispose()
    }

    companion object {
        fun newInstance() =
            CurrencyFragment()
    }

    /**
     * Interface to handle callbacks
     * */
    interface OnCurrencyFragmentListener {
        /**
         * Function to handle callback to show progress bar .
         * */
        fun showProgressBar()

        /**
         * Function to handle callback to hide progress bar .
         * */
        fun hideProgressBar()
    }
}
