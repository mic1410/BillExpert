package pl.szkoleniaandroid.billexpert.features.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.databinding.ActivityLoginBinding
import pl.szkoleniaandroid.billexpert.domain.usecase.SignInUseCase
import pl.szkoleniaandroid.billexpert.utils.LiveEvent
import pl.szkoleniaandroid.billexpert.utils.ObservableString
import pl.szkoleniaandroid.billexpert.utils.StringProvider

class SignInFragment : Fragment() {

    private val viewModel by viewModel<SignInViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ActivityLoginBinding.inflate(inflater, container, false).apply {
            this.vm = viewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //me: this will be automatically unregistered when fragment (this) is destroyed
        viewModel.showErrorLiveData.observe(this, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
        viewModel.goToBillsEvent.observe(this) {
            findNavController().navigate(SignInFragmentDirections.navSignedIn())

        }

    }
}

class SignInViewModel(
        private val stringProvider: StringProvider,
        private val signInUseCase: SignInUseCase
) : ViewModel() {

    val username = ObservableString("test")
    val usernameError = ObservableString("")
    val password = ObservableString("pass")
    val passwordError = ObservableString("")
    val inProgress = ObservableBoolean(false)

    //me: we use LiveEvent class here which emits data just once
    val showErrorLiveData = LiveEvent<String>()
    val goToBillsEvent = LiveEvent<Unit>()

    fun loginClicked() {
        var isValid = true
        if (username.get().isNullOrEmpty()) {
            //me: in pure Clean Architecture String should also be provided by e.g. new StringErrorProvider class
            usernameError.set(stringProvider.getString(R.string.username_cant_be_empty))
            isValid = false
        }

        if (isValid) {
            viewModelScope.launch {

                val result = signInUseCase(
                        username = username.get()!!,
                        password = password.get()!!
                )

                //me: in MVP here would be interface with methods to notify user in UI
                if (result) {
                    //GO TO BILLS
                    goToBillsEvent.value = Unit

                } else {
                    //me: in pure Clean Architecture String should also be provided by e.g. new StringErrorProvider class
                    showErrorLiveData.value = stringProvider.getString(R.string.invalid_credentials)
                    //SHOW ERROR
                }

            }
        }
    }
}