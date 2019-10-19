package pl.szkoleniaandroid.billexpert.features.bills

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

import pl.szkoleniaandroid.billexpert.features.signin.LoginActivity
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.databinding.ActivityBillsBinding
import pl.szkoleniaandroid.billexpert.features.billdetails.BillDetailsActivity
import pl.szkoleniaandroid.billexpert.repository.SessionRepository
import pl.szkoleniaandroid.billexpert.security.TamperStatus
import pl.szkoleniaandroid.billexpert.security.antiTamperCheck

class BillsActivity : AppCompatActivity() {

    private val binding: ActivityBillsBinding by lazy {
        DataBindingUtil.setContentView<ActivityBillsBinding>(this@BillsActivity, R.layout.activity_bills)
    }
    private val sessionRepository: SessionRepository by inject()
    private val viewModel: BillsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tamperStatus = antiTamperCheck(this)
        if (tamperStatus != TamperStatus.OK) {
            showTamperDialog(tamperStatus)
        } else {
            GlobalScope.launch {
                val currentUser = sessionRepository.loadCurrentUser()
                withContext(Dispatchers.Main) {
                    if (currentUser == null) {
                        goToLogin()
                        return@withContext
                    }
                    binding.model = viewModel
                    binding.listener = Runnable { showNewBill() }
                    binding.setLifecycleOwner(this@BillsActivity)
                    setSupportActionBar(binding.toolbar)
                }
            }
        }
    }

    private fun showTamperDialog(tamperStatus: TamperStatus) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.insecure_environment_error_title))
                .setMessage(getString(R.string.insecure_environment_message, tamperStatus.message))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    finish()
                }
                .show()
    }

    fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showNewBill() {
        startActivityForResult(Intent(this, BillDetailsActivity::class.java), REQUEST_ADD)
    }

    companion object {
        const val REQUEST_ADD = 1
    }
}
