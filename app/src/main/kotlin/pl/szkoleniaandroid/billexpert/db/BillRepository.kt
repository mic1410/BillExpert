package pl.szkoleniaandroid.billexpert.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import pl.szkoleniaandroid.billexpert.api.Bill

interface BillRepository {
    fun getBills(userId: String): LiveData<List<Bill>>
    fun getTotalAmount(userId: String): LiveData<Double>
    suspend fun getMaxUpdatedAt(userId: String): LocalDateTime
    suspend fun saveBill(bill: Bill)
    suspend fun saveBills(bills: List<Bill>)
    suspend fun updateBill(bill: Bill)
    suspend fun deleteBill(bill: Bill)
}

class BillRoomRepository(private val billDao: BillDao) : BillRepository {
    override suspend fun getMaxUpdatedAt(userId: String): LocalDateTime {
        return GlobalScope.async {
            billDao.getMaxUpdatedAt(userId)?.value ?: LocalDateTime.MIN
        }.await()
    }

    override suspend fun updateBill(bill: Bill) {
        GlobalScope.async {
            billDao.update(bill.toDto())
        }.await()
    }

    override fun getTotalAmount(userId: String) = Transformations.map(
            billDao.getTotalAmountForUser(userId)
    ) { it.value }!!

    override fun getBills(userId: String): LiveData<List<Bill>> {
        return Transformations.map(billDao.getAllForUser(userId)) {
            it.map { it.toBill() }
        }
    }

    override suspend fun saveBill(bill: Bill) {
        GlobalScope.async {
            billDao.insert(bill.toDto())
        }.await()
    }

    override suspend fun saveBills(bills: List<Bill>) {
        GlobalScope.async {
            billDao.insert(bills.map { it.toDto() }.toList())
        }.await()
    }

    override suspend fun deleteBill(bill: Bill) {
        GlobalScope.async {
            billDao.deleteBill(bill.toDto())
        }.await()
    }
}

fun BillDto.toBill(): Bill = Bill(
        userId = userId,
        date = date,
        name = name,
        amount = amount,
        category = category,
        comment = comment,
        objectId = objectId,
        createdAt = createdAt,
        updatedAt = updatedAt
)

fun Bill.toDto(): BillDto = BillDto().let {
    it.objectId = objectId
    it.userId = userId
    it.date = date
    it.name = name
    it.amount = amount
    it.category = category
    it.comment = comment
    it.createdAt = createdAt ?: LocalDateTime.MIN
    it.updatedAt = updatedAt ?: LocalDateTime.MIN
    return it
}
