package pl.szkoleniaandroid.billexpert.domain.usecase

import pl.szkoleniaandroid.billexpert.domain.repositories.LocalRepository
import pl.szkoleniaandroid.billexpert.domain.repositories.RemoteRepository

class SignInUseCase(
        private val remoteRepository: RemoteRepository,
        private val localRepository: LocalRepository
) {

    suspend operator fun invoke(username: String, password: String): Boolean {
        val user = remoteRepository.login(username, password)
        localRepository.saveUser(user)
        return true
    }
}