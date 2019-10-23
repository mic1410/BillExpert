package pl.szkoleniaandroid.billexpert.features.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.bindingcollectionadapter2.OnItemBind
import org.json.JSONObject
import pl.szkoleniaandroid.billexpert.BR
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.db.BillRepository
import pl.szkoleniaandroid.billexpert.di.LOCAL_DATE_TIME_FORMATTER
import pl.szkoleniaandroid.billexpert.repository.SessionRepository
import timber.log.Timber
import java.net.UnknownHostException

class BillsListViewModel(
        private val billApi: BillApi,
        private val sessionRepository: SessionRepository,
        private val billRepository: BillRepository
) : ViewModel() {

    val totalAmount: LiveData<Double> =
            billRepository.getTotalAmount(sessionRepository.currentUser!!.objectId)

    val bills = billRepository.getBills(sessionRepository.currentUser!!.objectId, { loadBills() })

    val billsLiveData: LiveData<List<Item>> = Transformations.map(bills) {
        it.map {
            BillItem(
                    name = it.name,
                    comment = it.comment,
                    amount = it.amount,
                    categoryUrl = "file:///android_asset/${it.category.name.toLowerCase()}.png",
                    bill = it
            )
        }
    }
    val isLoadingLiveData = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(billsLiveData) { bills ->
            setValue(bills.isEmpty())
        }
        addSource(isLoadingLiveData) { loading ->
            if (loading) {
                value = false
            }
        }
    }

    val itemBinding: OnItemBind<Item> = OnItemBind { itemBinding, _, item ->
        when (item) {
            is CategoryItem -> itemBinding.set(BR.item, R.layout.category_item)
            is BillItem -> {
                itemBinding.set(BR.item, R.layout.bill_item)
                itemBinding.bindExtra(BR.listener, object : OnBillClickedListener {
                    override fun onBillClicked(bill: BillItem) {
                        view?.editBill(bill.bill)
                    }
                })
            }
        }
    }

    var view: BillsView? = null

    fun loadBills() {

        isLoadingLiveData.value = true
        GlobalScope.launch(Dispatchers.Main) {

            sessionRepository.currentUser?.objectId?.let { userId ->

                val maxUpdatedAt = billRepository.getMaxUpdatedAt(userId)
                val where = JSONObject()
                where.put("userId", userId)
                val updatedAtWhere = JSONObject()
                val isoDate = LOCAL_DATE_TIME_FORMATTER.format(maxUpdatedAt)
                val jsonDate = JSONObject()
                jsonDate.put("__type", "Date")
                jsonDate.put("iso", isoDate)
                updatedAtWhere.put("\$gte", jsonDate)
                where.put("updatedAt", updatedAtWhere)
                try {

                    val bills = billApi.getBillsForUser(where.toString(), limit = 3).results
                    GlobalScope.launch {
                        billRepository.saveBills(bills)
                        withContext(Dispatchers.Main) {
                            isLoadingLiveData.value = false
                        }
                    }
                } catch (e: UnknownHostException) {
                    Timber.e(e)
                    //TODO handle errors
                }
            }


        }

    }

    fun logout() {
        sessionRepository.clearCurrentUser()
    }

    fun getCsvBody(): String {
        val sb = StringBuilder()
        sb.append("NAME,CATEGORY,AMOUNT,DATE\n")
        bills.value?.forEach { bill ->
            sb.append(bill.name)
            sb.append(',')
            sb.append(bill.category)
            sb.append(',')
            sb.append(bill.amount)
            sb.append(',')
            sb.append(bill.date)
            sb.append('\n')
        }
        return sb.toString()
    }
}
