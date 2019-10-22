package pl.szkoleniaandroid.billexpert.utils

import android.content.Context
import androidx.databinding.ObservableField

interface StringProvider {
    fun getString(res: Int, vararg formatArgs: Any): String
}

class ContextStringProvider(private val context: Context) : StringProvider {
    @Suppress("SpreadOperator")
    override fun getString(res: Int, vararg formatArgs: Any): String {
        return context.getString(res, *formatArgs)
    }
}

typealias ObservableString = ObservableField<String>

