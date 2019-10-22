package pl.szkoleniaandroid.billexpert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import pl.szkoleniaandroid.billexpert.repository.SessionRepository
import pl.szkoleniaandroid.billexpert.security.TamperStatus
import pl.szkoleniaandroid.billexpert.security.antiTamperCheck

class BillExpertActivity : AppCompatActivity() {

    private val sessionRepository: SessionRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.bill_expert_activity)
        val tamperStatus = antiTamperCheck(this)
        if (tamperStatus != TamperStatus.OK) {
            showTamperDialog(tamperStatus)
        } else {
            GlobalScope.launch {
                val currentUser = sessionRepository.loadCurrentUser()
                withContext(Dispatchers.Main) {
                    val navController = findNavController(R.id.nav_host)
                    if (currentUser == null) {
                        navController.navigate(SplashFragmentDirections.navSplashNotSignedIn())
                    } else {
                        navController.navigate(SplashFragmentDirections.navSplashSignedIn())
                    }
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

}
