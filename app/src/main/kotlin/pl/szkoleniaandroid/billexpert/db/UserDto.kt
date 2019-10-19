package pl.szkoleniaandroid.billexpert.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class UserDto {
    @PrimaryKey
    var objectId: String = ""
    var username: String = ""
    var password: String = ""
    var token: String = ""
}
