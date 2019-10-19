package pl.szkoleniaandroid.billexpert.features.signin

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.db.User
import pl.szkoleniaandroid.billexpert.db.UserRepository
import pl.szkoleniaandroid.billexpert.repository.SessionRepository
import pl.szkoleniaandroid.billexpert.security.hash
import pl.szkoleniaandroid.billexpert.utils.Event
import timber.log.Timber
import java.io.IOException

class LoginViewModel(
        val billApi: BillApi,
        val stringProvider: StringProvider,
        val sessionRepository: SessionRepository,
        val userRepository: UserRepository
) : ViewModel() {
    val username = ObservableString("")
    val password = ObservableString("")
    val usernameError = ObservableString("")
    var passwordError = ObservableString("")
    val inProgress = ObservableBoolean(false)

    val uiState = MutableLiveData<LoginUiModel>()

    private var loginJob: Job? = null

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

        if (loginJob?.isActive == true) return
        inProgress.set(true)
        uiState.value = LoginInProgress

        loginJob = GlobalScope.launch {
            try {
                val response = billApi.getLogin(usernameString, passwordString).await()
                Timber.d(response.toString())
                if (response.isSuccessful) {
                    val loginResponse = response.body()!!
                    val user = User(
                            objectId = loginResponse.objectId,
                            username = loginResponse.username,
                            password = passwordString.hash(),
                            token = loginResponse.sessionToken
                    )
                    userRepository.save(user)
                    sessionRepository.saveCurrentUser(user)
                    withContext(Dispatchers.Main) {
                        uiState.value = LoginSuccessful()
                        inProgress.set(false)
                    }
                } else {
                    val error = response.errorBody()
                    withContext(Dispatchers.Main) {
                        uiState.value = LoginError(Event("Some error from api"))
                        inProgress.set(false)
                    }
                }
            } catch (e: IOException) {
                Timber.e(e)
                withContext(Dispatchers.Main) {
                    uiState.value = LoginError(Event("Some connection error"))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        loginJob?.cancel()
    }
}

sealed class LoginUiModel

object LoginInProgress : LoginUiModel()
data class LoginError(val error: Event<String>) : LoginUiModel()
data class LoginSuccessful(val showSuccess: Event<Unit> = Event(Unit)) : LoginUiModel()
