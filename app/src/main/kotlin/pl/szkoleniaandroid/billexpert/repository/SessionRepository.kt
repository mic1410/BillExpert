package pl.szkoleniaandroid.billexpert.repository

import android.content.SharedPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.szkoleniaandroid.billexpert.db.User
import pl.szkoleniaandroid.billexpert.db.UserRepository

interface SessionRepository {
    val currentUser: User?

    fun saveCurrentUser(user: User)
    fun clearCurrentUser()
    suspend fun loadCurrentUser(): User?
}

class SessionRepositoryImpl(
        private val sharedPreferences: SharedPreferences,
        private val userRepository: UserRepository
) : SessionRepository {

    private var user: User? = null

    override val currentUser: User?
        get() = user

    override fun clearCurrentUser() {
        user = null
        sharedPreferences.edit().remove(USER_ID_KEY).apply()
    }

    override fun saveCurrentUser(user: User) {
        this.user = user
        sharedPreferences.edit().putString(USER_ID_KEY, user.objectId).apply()
    }

    override suspend fun loadCurrentUser(): User? {
        val userId = sharedPreferences.getString(USER_ID_KEY, "")!!
        if (userId.isNotEmpty()) {
            user = userRepository.getUserById(userId)
        }
        return user
    }

    companion object {
        const val USER_ID_KEY = "user_id"
    }
}
