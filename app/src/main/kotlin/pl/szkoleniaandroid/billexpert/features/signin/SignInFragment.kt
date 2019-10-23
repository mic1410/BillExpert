package pl.szkoleniaandroid.billexpert.features.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.databinding.ActivityLoginBinding
import pl.szkoleniaandroid.billexpert.domain.usecase.SignInUseCase
import pl.szkoleniaandroid.billexpert.utils.ContextStringProvider
import pl.szkoleniaandroid.billexpert.utils.ObservableString

class SignInFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //data binding, will make login screen show
        // return ActivityLoginBinding.inflate(inflater, container, false).root

        return ActivityLoginBinding.inflate(inflater, container, false).apply {
            this.vm = SignInViewModel(
                    stringProvider = ContextStringProvider(requireContext()),
                    signInUseCase = SignInUseCase()
            )
        }.root
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

    fun loginClicked() {
        var isValid = true
        if (username.get().isNullOrEmpty()) {
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
                //SHOW ERROR
            }

        }

    }

}



