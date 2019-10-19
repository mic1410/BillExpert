package pl.szkoleniaandroid.billexpert.features.billdetails

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.databinding.ActivityBillDetailsBinding
import pl.szkoleniaandroid.billexpert.utils.EventObserver

class BillDetailsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val binding: ActivityBillDetailsBinding by lazy {
        DataBindingUtil.setContentView<ActivityBillDetailsBinding>(this, R.layout.activity_bill_details)
    }
    private val viewModel: BillDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bill = intent.getSerializableExtra(BILL_EXTRA) as Bill?
        if (bill != null) {
            viewModel.setBill(bill)
        }
        binding.model = viewModel
        viewModel.pickDate.observe(this, EventObserver {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(supportFragmentManager, DIALOG_TAG)
        })
        viewModel.savedLiveData.observe(this, EventObserver {
            val intent = Intent()
            intent.putExtra(BILL_EXTRA, it)
            setResult(Activity.RESULT_OK, intent)
            finish()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bill_details_menu, menu)
        return super.onCreateOptionsMenu(menu)
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

    companion object {
        const val BILL_EXTRA = "bill"
        const val DIALOG_TAG = "date_picker"
    }
}
