package pl.szkoleniaandroid.billexpert

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("dateToDisplay")
fun bindDate(textView: TextView, date: Date) {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    textView.text = format.format(date)
}

@BindingAdapter("imageUrl")
fun bindUrl(imageView: ImageView, url: String?) {
    Picasso.get().load(url)
            .into(imageView)
}

@BindingAdapter("visible")
fun visible(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingConversion
fun doubleToString(value: Double?): String {
    return String.format("%.2f", value ?: 0.0)
}

