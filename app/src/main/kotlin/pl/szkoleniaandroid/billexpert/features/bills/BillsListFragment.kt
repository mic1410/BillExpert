package pl.szkoleniaandroid.billexpert.features.bills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.OnItemBind
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.BR
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.data.db.BillDao
import pl.szkoleniaandroid.billexpert.data.db.BillDto
import pl.szkoleniaandroid.billexpert.databinding.BillsListFragmentBinding
import pl.szkoleniaandroid.billexpert.domain.repositories.BillRepository
import pl.szkoleniaandroid.billexpert.domain.repositories.LocalRepository
import timber.log.Timber
import java.net.UnknownHostException

class BillsListFragment : Fragment() {

    private val viewModel by viewModel<BillsListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return BillsListFragmentBinding.inflate(inflater, container, false).apply {

            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            swiperefresh.setOnRefreshListener {
                viewModel.loadBills(true)
            }

        }.root
    }

}
class BillsListViewModel(
        private val billApi: BillApi,
        private val billRepository: BillRepository,
        private val localRepository: LocalRepository,
        private val billDao: BillDao
) : ViewModel() {

    val userId = localRepository.getUserId()

    val map: DataSource.Factory<Int, BillItem> = billDao.getAllForUser(userId)
            .map { it.toBillItem() }
    val pagedList: LiveData<PagedList<BillItem>> = map.toLiveData(3, boundaryCallback = object :PagedList.BoundaryCallback<BillItem>() {
        override fun onZeroItemsLoaded() {
            super.onZeroItemsLoaded()
            loadBills(true)
        }

        override fun onItemAtEndLoaded(itemAtEnd: BillItem) {
            super.onItemAtEndLoaded(itemAtEnd)
            loadBills(true)
        }
    });
//    val pagedList: LiveData<PagedList<BillItem>> = LivePagedListBuilder<Int, BillItem>(map,
//            Config(3)).build();

    val diff = object : DiffUtil.ItemCallback<BillItem>() {
        override fun areItemsTheSame(oldItem: BillItem, newItem: BillItem): Boolean {
            return oldItem == newItem //id
        }

        override fun areContentsTheSame(oldItem: BillItem, newItem: BillItem): Boolean {
            return oldItem == newItem
        }

    }


    val isLoadingLiveData = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(pagedList) { bills ->
            setValue(bills.isEmpty())
        }
        addSource(isLoadingLiveData) { loading ->
            if (loading) {
                value = false
            }
        }
    }

    val itemBinding: OnItemBind<BillItem> = OnItemBind { itemBinding, _, item ->
        itemBinding.set(BR.item, R.layout.bill_item)
//        itemBinding.bindExtra(BR.listener, object : OnBillClickedListener {
//            override fun onBillClicked(bill: BillItem) {
//                view?.editBill(bill.bill)
//            }
//        })
    }

    var view: BillsView? = null

    fun loadBills(force: Boolean) {

        isLoadingLiveData.value = true
        GlobalScope.launch(Dispatchers.Main) {


                val where = JSONObject()
                where.put("userId", userId)
//                val updatedAtWhere = JSONObject()
//                val isoDate = LOCAL_DATE_TIME_FORMATTER.format(maxUpdatedAt)
//                val jsonDate = JSONObject()
//                jsonDate.put("__type", "Date")
//                jsonDate.put("iso", isoDate)
//
//                updatedAtWhere.put(if (force) "\$gte" else "\$eq", jsonDate)
//                if (force) {
//                    where.put("updatedAt", updatedAtWhere)
//                } else {
//                    where.put("date", )
//                }
//
                val skip = billRepository.getBillsCount(userId)
                try {

                    val bills = billApi.getBillsForUser(where.toString(), limit = 3, order = "-date", skip = skip).results
                    Timber.d("loadBills downloaded bills, skip: %s, %s", skip, bills.toString())
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

    fun getCsvBody(): String {
        val sb = StringBuilder()
        sb.append("NAME,CATEGORY,AMOUNT,DATE\n")
//        pagedList.value?.forEach { bill ->
//            sb.append(bill.name)
//            sb.append(',')
//            sb.append(bill.bill.category)
//            sb.append(',')
//            sb.append(bill.amount)
//            sb.append(',')
//            sb.append(bill.bill.date)
//            sb.append('\n')
//        }
        return sb.toString()
    }
}

fun BillDto.toBillItem(): BillItem {
    return BillItem(
            name = this.name,
            comment = this.comment,
            amount = this.amount,
            categoryUrl = "file:///android_asset/${this.category.name.toLowerCase()}.png"
    )
}


interface BillsView {
    fun editBill(bill: Bill)
}

interface OnBillClickedListener {
    fun onBillClicked(bill: BillItem)
}

sealed class Item

data class BillItem(
        val name: String,
        val comment: String,
        val amount: Double,
        val categoryUrl: String
) : Item()

class CategoryItem(val categoryName: String) : Item()
