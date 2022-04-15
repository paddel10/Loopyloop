package ch.patland.loopyloop.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter

class VideoSizeBindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["video_size"], requireAll = false)
        fun loadItemName(view: TextView, videoSize: String) {
            view.text = videoSize
        }
    }
}