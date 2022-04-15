package ch.patland.loopyloop.utils

import android.text.format.DateUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter

class VideoDurationBindingAdpater {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["video_duration"], requireAll = false)
        fun loadVideoDuration(view: TextView, videoDuration: Long) {
            view.text = DateUtils.formatElapsedTime(videoDuration / 1000)
        }
    }
}