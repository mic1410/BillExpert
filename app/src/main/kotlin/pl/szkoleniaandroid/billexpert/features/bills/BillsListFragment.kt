package pl.szkoleniaandroid.billexpert.features.bills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.BR
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.data.db.BillDao
import pl.szkoleniaandroid.billexpert.data.db.BillDto
import pl.szkoleniaandroid.billexpert.databinding.BillsListFragmentBinding
import pl.szkoleniaandroid.billexpert.domain.repositories.LocalRepository

class BillsListFragment : Fragment() {

    private val viewModel by viewModel<BillsListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return BillsListFragmentBinding.inflate(inflater, container, false).apply {

            vm = viewModel
            lifecycleOwner = viewLifecycleOwner


        }.root
    }

}

class BillsListViewModel(
        private val billApi: BillApi,
        private val billDao: BillDao,
        private val localRepository: LocalRepository
) : ViewModel() {

    //val bills = ObservableArrayList<BillItem>()

    val itemBinding = ItemBinding.of<BillItem>(BR.item, R.layout.bill_item)
    val userId = localRepository.getUserId()
    //me: initially display data loaded from DB
    val bills = billDao.getAllForUser(userId).map { it.map { dto -> dto.toUIModel() } }

    init {
        loadBills()
    }

    fun loadBills() {


        val whereJson = JSONObject()
        whereJson.put("userId", userId)
        //me: fetch new data from network and update DB which will cause bills list to update
        viewModelScope.launch {
            val billsFromApi = billApi.getBillsForUser(whereJson.toString(), 10)

            billDao.insert(
                    billsFromApi.results.map { bill ->
                        BillDto().apply {
                            objectId = bill.objectId
                            this.userId = bill.userId
                            date = bill.date
                            name = bill.name
                            amount = bill.amount
                            category = bill.category
                            comment = bill.comment
                            createdAt = bill.createdAt!!
                            updatedAt = bill.updatedAt!!
                        }
                    }
            )

        }
    }
}

fun BillDto.toUIModel() = BillItem(
        name = this.name,
        comment = this.comment,
        amount = this.amount,
        categoryUrl = "file:///android_asset/${this.category.name.toLowerCase()}.png"
)

data class BillItem(
        val name: String,
        val comment: String,
        val amount: Double,
        val categoryUrl: String
)


class CategoryItem