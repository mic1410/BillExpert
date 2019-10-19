package pl.szkoleniaandroid.billexpert.db

import pl.szkoleniaandroid.billexpert.security.hash

interface UserRepository {

    fun save(user: User)

    fun getUserByAuthData(username: String, password: String): User?

    fun getUserById(userId: String): User
}

class RoomUserRepository(private val userDao: UserDao) : UserRepository {

    override fun save(user: User) {
        userDao.insert(
                UserDto().apply {
                    this.objectId = user.objectId
                    this.username = user.username
                    this.password = user.password
                    this.token = user.token
                }
        )
    }

    override fun getUserByAuthData(username: String, password: String): User? {
        //keeping passwords LOCALLY in MD5, WITHOUT SALT is as #INSECURE as it can get
        val md5Password = password.hash()
        val userDto = userDao.getUserByCredentials(username, md5Password)
                ?: return null
        return User(
                objectId = userDto.objectId,
                username = userDto.username,
                password = userDto.password,
                token = userDto.token
        )
    }

    override fun getUserById(userId: String): User {
        val userDto = userDao.getUserById(userId)
        return User(
                objectId = userDto.objectId,
                username = userDto.username,
                password = userDto.password,
                token = userDto.token
        )
    }
}

data class User(
        val objectId: String,
        val username: String,
        val password: String,
        val token: String
)
