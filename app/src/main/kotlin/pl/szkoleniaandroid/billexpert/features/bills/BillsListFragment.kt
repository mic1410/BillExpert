package pl.szkoleniaandroid.billexpert.features.bills

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.CsvFileProvider
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.databinding.FragmentBillsBinding
import pl.szkoleniaandroid.billexpert.features.billdetails.BillDetailsActivity

class BillsListFragment : Fragment(), BillsView {

    lateinit var binding: FragmentBillsBinding
    private val viewModel: BillsListViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentBillsBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.setLifecycleOwner(this)
        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadBills()
        }
        return binding.root
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadBills()
    }

    override fun onStart() {
        super.onStart()
        viewModel.view = this

    }

    override fun onStop() {
        super.onStop()
        viewModel.view = null
    }


    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_bills, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.loadBills()
                true
            }
            R.id.action_export -> {
                val csvBody = viewModel.getCsvBody()
                CsvFileProvider.showCsv(activity!!, csvBody, "exported_bills.csv")
                true
            }
            R.id.action_logout -> {
                viewModel.logout()
                (activity as BillsActivity).goToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BillsActivity.REQUEST_ADD) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.loadBills()
            } else {
                Toast.makeText(activity, "Nothing added!", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun editBill(bill: Bill) {
        val intent = Intent(activity, BillDetailsActivity::class.java)
        intent.putExtra("bill", bill)
        activity!!.startActivity(intent)
    }

}

interface BillsView {
    fun editBill(bill: Bill)
}

interface OnBillClickedListener {
    fun onBillClicked(bill: BillItem)
}

sealed class Item

data class BillItem(
        val name: String,
        val comment: String,
        val amount: Double,
        val categoryUrl: String,
        val bill: Bill
) : Item()

class CategoryItem(val categoryName: String) : Item()
