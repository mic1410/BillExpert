package pl.szkoleniaandroid.billexpert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController

class BillExpertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.bill_expert_activity)
        val navController = findNavController(R.id.nav_host)
        navController.navigate(SplashFragmentDirections.navSplashNotSignedIn())
    }

}
