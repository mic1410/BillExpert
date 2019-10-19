package pl.szkoleniaandroid.billexpert.features.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.databinding.ActivityLoginBinding
import pl.szkoleniaandroid.billexpert.features.bills.BillsActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewmodel = viewModel
        viewModel.uiState.observe(this, Observer {
            when (it) {
                is LoginInProgress -> {
                }
                is LoginError -> {
                    if (!it.error.consumed) {
                        Toast.makeText(this, it.error.consume(), Toast.LENGTH_LONG).show()
                    }
                }
                is LoginSuccessful -> goToBills()
            }
        })
    }

    fun goToBills() {
        val intent = Intent(this, BillsActivity::class.java)
        startActivity(intent)
        finish()
    }

}

interface StringProvider {
    fun getString(res: Int, vararg formatArgs: Any): String
}

class ContextStringProvider(private val context: Context) : StringProvider {
    @Suppress("SpreadOperator")
    override fun getString(res: Int, vararg formatArgs: Any): String {
        return context.getString(res, *formatArgs)
    }
}

typealias ObservableString = ObservableField<String>

