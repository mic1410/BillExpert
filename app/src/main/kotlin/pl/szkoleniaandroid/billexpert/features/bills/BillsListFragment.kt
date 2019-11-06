package pl.szkoleniaandroid.billexpert.features.bills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.BR
import pl.szkoleniaandroid.billexpert.R
import pl.szkoleniaandroid.billexpert.databinding.BillsListFragmentBinding

class BillsListFragment : Fragment() {

    private val viewModel by viewModel<BillsListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return BillsListFragmentBinding.inflate(inflater, container, false).apply {
            vm = viewModel
        }.root
    }
}

class BillsListViewModel : ViewModel() {

    val bills = ObservableArrayList<BillItem>()

    val itemBinding = ItemBinding.of<BillItem>(BR.item, R.layout.bill_item)

    init {
        bills.addAll(
                (0..9).map {
                    BillItem("name$it", "comment", 10.3,
                            "https://placekitten.com/20$it/20$it")
                }.toList()
        )
    }
}

data class BillItem(
        val name: String,
        val comment: String,
        val amount: Double,
        val categoryUrl: String
)


class CategoryItem