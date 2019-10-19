package pl.szkoleniaandroid.billexpert.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import pl.szkoleniaandroid.billexpert.api.Category
import pl.szkoleniaandroid.billexpert.di.LOCAL_DATE_TIME_FORMATTER
import pl.szkoleniaandroid.billexpert.di.MIN_DATE_TIME
import java.util.*

@Entity(tableName = "bill")
@TypeConverters(Converters::class)
class BillDto {
    @PrimaryKey
    var objectId: String = ""
    @ForeignKey(entity = UserDto::class, parentColumns = ["objectId"],
            childColumns = ["userId"])
    var userId: String = ""
    var date: Date = Date()
    var name: String = ""
    var amount: Double = 0.0
    var category: Category = Category.OTHER
    var comment: String = ""
    var createdAt: LocalDateTime = LocalDateTime.MIN
    var updatedAt: LocalDateTime = LocalDateTime.MIN
}

class Converters {

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun fromDateTime(date: LocalDateTime): String = LOCAL_DATE_TIME_FORMATTER.format(date)

    @TypeConverter
    fun toDateTime(format: String?): LocalDateTime = format?.let {
        LocalDateTime.parse(format, LOCAL_DATE_TIME_FORMATTER)
    } ?: MIN_DATE_TIME

    @TypeConverter
    fun fromCategory(category: Category): Int = category.ordinal

    @TypeConverter
    fun toCategory(index: Int): Category = Category.values()[index]
}
