package pl.szkoleniaandroid.billexpert.features.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.databinding.ActivityLoginBinding
import pl.szkoleniaandroid.billexpert.domain.usecase.SignInUseCase
import pl.szkoleniaandroid.billexpert.utils.ContextStringProvider
import pl.szkoleniaandroid.billexpert.utils.ObservableString

class SignInFragment : Fragment() {

    lateinit var viewModel: SignInViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //data binding, will make login screen show
        // return ActivityLoginBinding.inflate(inflater, container, false).root

        return ActivityLoginBinding.inflate(inflater, container, false).apply {
            viewModel = SignInViewModel(
                    stringProvider = ContextStringProvider(requireContext()),
                    signInUseCase = SignInUseCase()
            )
            this.vm = viewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //me: this will be automatically unregistered when fragment (this) is destroyed
        viewModel.showErrorLiveData.observe(this, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }

}

class SignInViewModel(
        private val stringProvider: ContextStringProvider,
        private var signInUseCase: SignInUseCase
) {

    val username = ObservableString("")
    val usernameError = ObservableString("")
    val password = ObservableString("")
    val passwordError = ObservableString("")
    val inProgress = ObservableBoolean(false)

    val showErrorLiveData = MutableLiveData<String>("")

    fun loginClicked() {
        var isValid = true
        if (username.get().isNullOrEmpty()) {
            //me: in pure Clean Architecture String should also be provided by e.g. new StringErrorProvider class
            usernameError.set(stringProvider.getString(R.string.username_cant_be_empty))
            isValid = false
        }

        if (isValid) {
            val result = signInUseCase(
                    username = username.get()!!,
                    password = password.get()!!
            )

            //me: in MVP here would be interface with methods to notify user in UI
            if (result) {
                //GO TO BILLS
            } else {
                //me: in pure Clean Architecture String should also be provided by e.g. new StringErrorProvider class
                showErrorLiveData.value = stringProvider.getString(R.string.invalid_credentials)
                //SHOW ERROR
            }

        }

    }

}



