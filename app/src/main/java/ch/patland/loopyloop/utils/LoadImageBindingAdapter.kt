package ch.patland.loopyloop.utils

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import ch.patland.loopyloop.R

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class LoadImageBindingAdapter {
    companion object{
        @JvmStatic
        @BindingAdapter(value = ["video_uri", "video_duration", "display_name", "error"], requireAll = false)
        fun loadImage(view: ImageView, profileImage: Uri?, videoDuration: Long, displayName: String, error: Int) {

            // adjust start depending on duration - this to avoid black thumbnails
            var startFrame: Long = 0
            if (videoDuration > 30000) {
                startFrame = 30000000
            }
            GlideApp.with(view.context)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.white_background)
                        .error(R.drawable.white_background)
                        .frame(startFrame)
                        .fitCenter()
                )
                .load(profileImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view)
        }
    }
}