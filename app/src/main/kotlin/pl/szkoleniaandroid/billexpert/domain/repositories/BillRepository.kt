package pl.szkoleniaandroid.billexpert.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDateTime
import pl.szkoleniaandroid.billexpert.api.Bill

interface BillRepository {
    fun getTotalAmount(userId: String): Flow<Double>
    suspend fun getMaxUpdatedAt(userId: String): LocalDateTime
    suspend fun getMinUpdatedAt(userId: String): LocalDateTime

    suspend fun getBillsCount(userId: String): Int
    suspend fun saveBill(bill: Bill)
    suspend fun saveBills(bills: List<Bill>)
    suspend fun updateBill(bill: Bill)
    suspend fun deleteBill(bill: Bill)
}
