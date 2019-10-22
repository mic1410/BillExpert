package pl.szkoleniaandroid.billexpert.features.bills

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.CsvFileProvider
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.databinding.FragmentBillsBinding
import pl.szkoleniaandroid.billexpert.features.billdetails.BillDetailsFragmentArgs

class BillsListFragment : Fragment(), BillsView {

    lateinit var binding: FragmentBillsBinding
    private val viewModel: BillsListViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentBillsBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadBills()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
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


    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
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
                findNavController().navigate(BillsListFragmentDirections.navLoggedOut())
                //(activity as BillsActivity).goToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun editBill(bill: Bill) {
        val bundle = BillDetailsFragmentArgs.Builder()
                .setBill(bill)
                .build().toBundle()
        findNavController().navigate(R.id.nav_details, bundle)
//        val intent = Intent(activity, BillDetailsActivity::class.java)
//        intent.putExtra("bill", bill)
//        activity!!.startActivity(intent)
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
