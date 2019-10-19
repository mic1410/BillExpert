package pl.szkoleniaandroid.billexpert.features.billdetails

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.api.Category
import pl.szkoleniaandroid.billexpert.api.PostBillResponse
import pl.szkoleniaandroid.billexpert.api.PutBillResponse
import pl.szkoleniaandroid.billexpert.db.BillRepository
import pl.szkoleniaandroid.billexpert.features.signin.ObservableString
import pl.szkoleniaandroid.billexpert.repository.SessionRepository
import pl.szkoleniaandroid.billexpert.utils.Event
import retrofit2.Response
import java.util.*

class BillDetailsViewModel(private val billApi: BillApi,
                           private val sessionRepository: SessionRepository,
                           private val billRepository: BillRepository) : ViewModel() {

    private var originalBill = Bill(
            userId = sessionRepository.currentUser!!.objectId
    )
    val date = ObservableField<Date>(originalBill.date)
    val name = ObservableString(originalBill.name)
    val amount = ObservableString(originalBill.amount.toString())
    val comment = ObservableString(originalBill.comment)
    val nameError: ObservableField<String> = ObservableField("")
    val amountError: ObservableField<String> = ObservableField("")
    val hasComment: ObservableBoolean = ObservableBoolean(originalBill.comment.isNotBlank())
    val categories = Category.values().toList()
    val selectedCategoryIndex = ObservableInt(originalBill.category.ordinal)
    val pickDate = MutableLiveData<Event<Unit>>()
    val savedLiveData = MutableLiveData<Event<Bill>>()

    fun setBill(bill: Bill) {
        originalBill = bill
        name.set(originalBill.name)
        amount.set(originalBill.amount.toString())
        comment.set(originalBill.comment)
        date.set(originalBill.date)
        selectedCategoryIndex.set(originalBill.category.ordinal)
        hasComment.set(originalBill.comment.isNotBlank())
    }

    fun saveClicked() {

        var parsedAmount = 0.0
        var valid = true
        val nameString = this.name.get()!!
        if (nameString.isEmpty()) {
            valid = false
            nameError.set("Name can't be empty!")
        }
        val amountString = this.amount.get()!!
        if (amountString.isEmpty()) {
            valid = false
            amountError.set("Amount can't be empty!")
        } else {
            try {
                parsedAmount = amountString.toDouble()
                if (parsedAmount == 0.0) {
                    valid = false
                    amountError.set("Amount can't be zero!")
                }
            } catch (e: NumberFormatException) {
                valid = false
                amountError.set("Invalid amount!")
            }
        }

        if (valid) {
            submitBill(nameString, parsedAmount)
        }
    }

    private fun submitBill(nameString: String, parsedAmount: Double) {
        val category = Category.values()[selectedCategoryIndex.get()]
        val commentString = comment.get()!!
        val update = originalBill.objectId.isNotEmpty()
        val bill = Bill(
                userId = sessionRepository.currentUser!!.objectId,
                date = date.get()!!,
                name = nameString,
                amount = parsedAmount,
                category = category,
                comment = if (hasComment.get()) commentString else "",
                createdAt = if (update) originalBill.createdAt else null,
                updatedAt = if (update) originalBill.updatedAt else null,
                objectId = originalBill.objectId
        )

        if (update) {
            updateBill(bill)
        } else {
            createBill(bill)
        }
    }

    private fun createBill(bill: Bill) {
        val call: Deferred<Response<PostBillResponse>> = billApi.postBill(bill)
        GlobalScope.launch(Dispatchers.Main) {
            val response = call.await()
            if (response.isSuccessful) {
                launch {
                    val body = response.body()!!
                    val newBill = bill.copy(objectId = body.objectId, createdAt = body.createdAt,
                            updatedAt = body.createdAt)
                    billRepository.saveBill(newBill)
                    withContext(Dispatchers.Main) {
                        savedLiveData.value = Event(newBill)
                    }
                }
            }
        }
    }

    private fun updateBill(bill: Bill) {
        val call: Deferred<Response<PutBillResponse>> = billApi.putBill(bill, originalBill.objectId)
        GlobalScope.launch(Dispatchers.Main) {
            val response = call.await()
            if (response.isSuccessful) {
                launch {
                    billRepository.updateBill(bill.copy(updatedAt = response.body()!!.updatedAt))
                    withContext(Dispatchers.Main) {
                        savedLiveData.value = Event(bill)

                    }
                }
            }
        }
    }

    fun pickDateClicked() {
        pickDate.value = Event(Unit)
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        date.set(Date(year, month, dayOfMonth))
    }

    fun deleteBill() {
        GlobalScope.launch {
            val call: Deferred<Response<Any>> = billApi.deleteBill(originalBill.objectId)
            val response = call.await()
            if (response.isSuccessful) {
                billRepository.deleteBill(originalBill)
                withContext(Dispatchers.Main) {
                    savedLiveData.value = Event(originalBill)
                }
            }
        }
    }
}
