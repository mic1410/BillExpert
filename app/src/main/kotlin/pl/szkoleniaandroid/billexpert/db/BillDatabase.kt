package pl.szkoleniaandroid.billexpert.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BillDto::class, UserDto::class], version = 1)
abstract class BillDatabase : RoomDatabase() {

    abstract fun getBillDao(): BillDao

    abstract fun getUserDao(): UserDao

}
