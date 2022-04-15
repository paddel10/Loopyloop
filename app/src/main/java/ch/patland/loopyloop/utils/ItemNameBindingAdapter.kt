package ch.patland.loopyloop.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter

class ItemNameBindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["display_name"], requireAll = false)
        fun loadItemName(view: TextView, displayName: String) {
            view.text = displayName
        }
    }
}