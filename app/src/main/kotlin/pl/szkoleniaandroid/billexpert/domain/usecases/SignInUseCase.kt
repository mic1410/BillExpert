package pl.szkoleniaandroid.billexpert.domain.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.db.User
import pl.szkoleniaandroid.billexpert.db.UserRepository
import pl.szkoleniaandroid.billexpert.repository.BillsRemoteRepository
import pl.szkoleniaandroid.billexpert.repository.SessionRepository
import pl.szkoleniaandroid.billexpert.security.hash
import retrofit2.Converter
import retrofit2.HttpException
import kotlin.Exception

class SignInUseCase(
        private val billsRepository: BillsRemoteRepository,
        private val sessionRepository: SessionRepository,
        private val userRepository: UserRepository
) {

    suspend operator fun invoke(username: String, password: String): Unit {
        val user = billsRepository.login(username, password)
        userRepository.save(user)
        sessionRepository.saveCurrentUser(user)
    }
}

