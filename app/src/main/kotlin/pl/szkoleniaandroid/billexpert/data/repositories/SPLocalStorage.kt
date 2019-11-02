package pl.szkoleniaandroid.billexpert.data.repositories

import android.content.SharedPreferences
import pl.szkoleniaandroid.billexpert.domain.model.LoggedUser
import pl.szkoleniaandroid.billexpert.domain.repositories.LocalRepository

class SPLocalStorage(
        private val sharedPreferences: SharedPreferences
) : LocalRepository {

    override fun saveUser(user: LoggedUser) {
        sharedPreferences.edit().apply {
            putString("id", user.userId)
            putString("token", user.token)
            apply()
        }
    }
}