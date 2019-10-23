package pl.szkoleniaandroid.billexpert.domain.usecases

import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.db.BillRepository
import pl.szkoleniaandroid.billexpert.repository.BillsRemoteRepository

class UpdateBillUseCase(
        private val billsRepository: BillsRemoteRepository,
        private val billRepository: BillRepository
) {
    suspend operator fun invoke(bill: Bill) {
        val updatedBill = billsRepository.putBill(bill)
        billRepository.updateBill(updatedBill)
    }
}
