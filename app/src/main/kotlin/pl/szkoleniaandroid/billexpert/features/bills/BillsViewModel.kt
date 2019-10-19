package pl.szkoleniaandroid.billexpert.features.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import pl.szkoleniaandroid.billexpert.db.BillRepository
import pl.szkoleniaandroid.billexpert.repository.SessionRepository

class BillsViewModel(
        billRepository: BillRepository,
        sessionRepository: SessionRepository
) : ViewModel() {

    val totalAmount: LiveData<Double> = billRepository
            .getTotalAmount(sessionRepository.currentUser!!.objectId)
}
