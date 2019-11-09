package pl.szkoleniaandroid.billexpert.data.db

import androidx.paging.DataSource
import androidx.room.*
import org.threeten.bp.LocalDateTime

@Dao
@TypeConverters(Converters::class)
interface BillDao {

    @Insert
    fun insert(billDto: BillDto)

    @Query("SELECT * from bill where userId = :userId ORDER BY updatedAt DESC")
    fun getAllForUser(userId: String): DataSource.Factory<Int, BillDto>

    @Query("SELECT sum(amount) as value from bill where userId = :userId")
    fun getTotalAmountForUser(userId: String): Amount


    @Query("SELECT max(updatedAt) as value from bill where userId = :userId")
    fun getMaxUpdatedAt(userId: String): DateTimeWrapper?

    @Update
    fun update(billDto: BillDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(billDto: List<BillDto>)

    @Delete
    fun deleteBill(billDto: BillDto)
}

//wrapper class for sum in sql
class Amount(var value: Double)

class DateTimeWrapper(val value: LocalDateTime)