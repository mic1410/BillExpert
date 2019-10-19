package pl.szkoleniaandroid.billexpert.api

import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.util.*

class LoginResponse(
        val username: String,
        val objectId: String,
        val sessionToken: String
)

enum class Category {
    OTHER,
    BILLS,
    CAR,
    CHEMISTRY,
    CLOTHES,
    COSMETICS,
    ELECTRONICS,
    ENTERTAINMENT,
    FOOD,
    FURNITURE,
    GROCERIES,
    HEALTH,
    SHOES,
    SPORT,
    TOYS,
    TRAVEL
}

data class Bill(
        val userId: String,
        val date: Date = Date(),
        val name: String = "",
        val amount: Double = 0.0,
        val category: Category = Category.OTHER,
        val comment: String = "",
        val objectId: String = "",
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
) : Serializable

class BillsResponse(
        val results: List<Bill>
)

class PutBillResponse(
        val updatedAt: LocalDateTime
)

class PostBillResponse(val objectId: String, val createdAt: LocalDateTime)
