package pl.szkoleniaandroid.billexpert.domain.repositories

import pl.szkoleniaandroid.billexpert.domain.model.LoggedUser

interface RemoteRepository {

    suspend fun login(username: String, password: String): LoggedUser
}