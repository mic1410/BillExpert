package pl.szkoleniaandroid.billexpert.features.signin

import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import pl.szkoleniaandroid.billexpert.utils.ObservableString

class SignInFragment : Fragment()

class SignInViewModel {

    val username = ObservableString("")
    val usernameError = ObservableString("")
    val password = ObservableString("")
    val passwordError = ObservableString("")
    val inProgress = ObservableBoolean(false)

    fun loginClicked() {

    }

}



