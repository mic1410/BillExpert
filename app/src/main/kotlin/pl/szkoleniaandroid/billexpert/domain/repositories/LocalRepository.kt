package pl.szkoleniaandroid.billexpert.domain.repositories

import pl.szkoleniaandroid.billexpert.domain.model.LoggedUser

interface LocalRepository {

    fun saveUser(user: LoggedUser)
    fun getUserId(): String
}