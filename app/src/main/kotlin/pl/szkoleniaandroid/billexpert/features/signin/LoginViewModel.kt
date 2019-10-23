package pl.szkoleniaandroid.billexpert.features.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.domain.usecases.SignInUseCase
import pl.szkoleniaandroid.billexpert.repository.RepositoryError
import pl.szkoleniaandroid.billexpert.utils.LiveEvent
import pl.szkoleniaandroid.billexpert.utils.ObservableString
import pl.szkoleniaandroid.billexpert.utils.StringProvider

class LoginViewModel(
        private val stringProvider: StringProvider,
        private val signInUseCase: SignInUseCase
) : ViewModel() {
    val username = ObservableString("")
    val password = ObservableString("")
    val usernameError = ObservableString("")
    var passwordError = ObservableString("")
    val uiState = LiveEvent<LoginUiModel>()

    val inProgress = uiState.map { uiState.value == LoginInProgress }

    fun loginClicked() {
        var valid = true
        val usernameString = username.get()!!
        if (usernameString.isEmpty()) {
            usernameError.set(stringProvider.getString(R.string.username_cant_be_empty))
            valid = false
        }
        val passwordString = password.get()!!
        if (passwordString.isEmpty()) {
            passwordError.set(stringProvider.getString(R.string.password_cant_be_empty))
            valid = false
        }
        if (valid) {
            performLogin(usernameString, passwordString)
        }
    }

    private fun performLogin(usernameString: String, passwordString: String) {
        viewModelScope.launch {
            try {
                uiState.value = LoginInProgress
                signInUseCase(usernameString, passwordString)
                uiState.value = LoginSuccessful()
            } catch (error: RepositoryError) {
                uiState.value = LoginError(error.message!!)
            }
        }
    }
}

sealed class LoginUiModel

object LoginInProgress : LoginUiModel()
data class LoginError(val error: String) : LoginUiModel()
class LoginSuccessful : LoginUiModel()
