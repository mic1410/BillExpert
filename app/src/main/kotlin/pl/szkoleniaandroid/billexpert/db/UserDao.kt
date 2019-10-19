package pl.szkoleniaandroid.billexpert.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userDto: UserDto)

    @Query("SELECT * FROM user where username = :username and password = :password LIMIT 1")
    fun getUserByCredentials(username: String, password: String): UserDto?

    @Query("SELECT * FROM user WHERE objectId = :userId")
    fun getUserById(userId: String): User

}
