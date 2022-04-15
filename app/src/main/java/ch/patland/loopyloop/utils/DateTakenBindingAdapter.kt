package ch.patland.loopyloop.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter

class DateTakenBindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["date_taken"], requireAll = false)
        fun loadItemName(view: TextView, dateTaken: String) {
            view.text = dateTaken
        }
    }
}