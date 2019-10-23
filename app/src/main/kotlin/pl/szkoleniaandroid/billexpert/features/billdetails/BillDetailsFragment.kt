package pl.szkoleniaandroid.billexpert.features.billdetails

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_bills.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.databinding.ActivityBillDetailsBinding

class BillDetailsFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val viewModel: BillDetailsViewModel by viewModel()
    private val args by navArgs<BillDetailsFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ActivityBillDetailsBinding.inflate(inflater, container, false).apply {
            model = this@BillDetailsFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bill_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            viewModel.deleteBill()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.setDate(year, month, dayOfMonth)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        val bill = args.bill
        if (bill != null) {
            viewModel.setBill(bill)
        }
        viewModel.pickDate.observe(this, Observer {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(childFragmentManager, DIALOG_TAG)
        })
        viewModel.savedLiveData.observe(this, Observer {
            findNavController().popBackStack()
        })
    }

    companion object {
        const val DIALOG_TAG = "date_picker"
    }

}