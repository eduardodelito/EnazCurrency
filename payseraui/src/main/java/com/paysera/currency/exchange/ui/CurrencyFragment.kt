package com.paysera.currency.exchange.ui

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
import com.paysera.currency.exchange.common.util.CustomProgressBar
import javax.inject.Inject

/**
 * Created by eduardo.delito on 3/11/20.
 */
class CurrencyFragment : BaseFragment<CurrencyFragmentBinding, CurrencyViewModel>() {

    val progressBar = CustomProgressBar()

    @Inject
    override lateinit var viewModel: CurrencyViewModel

    override fun createLayout() = R.layout.currency_fragment

    override fun getBindingVariable() = BR.viewModel

    private lateinit var balancesAdapter: BalancesAdapter
    private var mCurrencies = ArrayList<String?>()

    override fun initData() {
        with(viewModel) {
            getPayseraResponse()
            getCurrencies()
        }
    }

    override fun initViews() {
        setHasOptionsMenu(true)
        balancesAdapter = BalancesAdapter()
        with(recycler_view) {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = balancesAdapter
        }

        context?.let { progressBar.show(it, "Please wait...") }

        drop_down_image.setOnClickListener {
            showDialog(mCurrencies, false)
        }
        drop_down_receive_image.setOnClickListener {
            showDialog(mCurrencies, true)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_reset -> {
                progressBar.dialog.show()
                viewModel.getPayseraResponse()
                viewModel.getCurrencies()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun subscribeUi() {
        with(viewModel) {

            errorMessage.observe(viewLifecycleOwner, Observer { messageId ->
                Toast.makeText(context, messageId?.let { getString(it) }, Toast.LENGTH_LONG).show()
            })

//            isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
//                if (!isLoading) progressBar.dialog.hide()
//            })

            isComputing.observe(viewLifecycleOwner, Observer { isComputing ->
                if (!isComputing) updateUI()
            })

            currencies.observe(viewLifecycleOwner, Observer { result ->
                mCurrencies = result
            })

            balanceListResult.observe(viewLifecycleOwner, Observer { result ->
                balancesAdapter.updateDataSet(result)
                progressBar.dialog.hide()
            })

            dialogMessage.observe(viewLifecycleOwner, Observer { message ->
                submitDialog(message)
            })

            currencyReceive.observe(viewLifecycleOwner, Observer { receive ->
                receive_text.text = "+ $receive"
            })
        }
    }

    override fun updateReceiveUI(selected: String?, isReceive: Boolean) {
        if (isReceive) {
            currency_receive_lbl?.text = selected
            receive_text.text = "+ 0"
        } else {
            currency_lbl.text = selected
        }
    }

    override fun updateBalanceUI() {
        viewModel.computeConvertedBalance(
            currency_lbl.text.toString(),
            currency_receive_lbl.text.toString(),
            balancesAdapter.getBaseBalance(currency_lbl.text.toString()),
            sell_field.text.toString()
            , false
        )
    }

    companion object {
        fun newInstance() =
            CurrencyFragment()
    }
}
