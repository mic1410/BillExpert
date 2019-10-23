package pl.szkoleniaandroid.billexpert.domain.usecases

import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.db.BillRepository
import pl.szkoleniaandroid.billexpert.repository.BillsRemoteRepository

class CreateBillUseCase(
        private val billsRepository: BillsRemoteRepository,
        private val billRepository: BillRepository
) {
    suspend operator fun invoke(bill: Bill) {
        val body = billsRepository.postBill(bill)
        val newBill = bill.copy(objectId = body.objectId, createdAt = body.createdAt,
                updatedAt = body.createdAt)
        billRepository.saveBill(newBill)

    }
}
