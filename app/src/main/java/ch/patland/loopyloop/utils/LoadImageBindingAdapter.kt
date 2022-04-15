package ch.patland.loopyloop.utils

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import ch.patland.loopyloop.GlideApp
import ch.patland.loopyloop.R
import ch.patland.loopyloop.utils.PlayerViewAdapter.Companion.loadVideo

import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout

class LoadImageBindingAdapter {
    companion object{
        @JvmStatic
        @BindingAdapter(value = ["video_uri", "video_duration", "error"], requireAll = false)
        fun loadImage(view: ImageView, profileImage: Uri?, videoDuration: Long, error: Int) {

            Log.d("LoadImageBindingAdapter", "loadImage()")
            // adjust start depending on duration - this to avoid black thumbnails
            var startFrame: Long = 0
            if (videoDuration > 30000) {
                Log.d("LoadImageBindingAdapter", "adjust frame")
                startFrame = 30000000
            }
            Glide.with(view.context)
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

        /*var startFrame: Long = 0
        if (item.duration > 30000) {
            Log.d(TAG, "adjust frame: " + item.displayName)
            startFrame = 30000000
        }
        val options: RequestOptions = RequestOptions()
            .frame(startFrame)
        requestManager.applyDefaultRequestOptions(options)
        requestManager
        .load(item.uri)
        .into(thumbnail)

        private fun initGlide(): RequestManager {
            val options = RequestOptions()
                .placeholder(R.drawable.picture)
                .fitCenter()
                // .override(1920, 1080)
                .error(R.drawable.picture)
            return GlideApp.with(this).setDefaultRequestOptions(options)
        }*/
    }
}