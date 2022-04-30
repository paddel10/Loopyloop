package ch.patland.loopyloop.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import ch.patland.loopyloop.media.MediaItem
import ch.patland.loopyloop.player.ThePlayer
import com.google.android.exoplayer2.ui.StyledPlayerView

// extension function for show toast
fun Context.toast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class PlayerViewAdapter() {

    companion object {
        private val TAG = "ItemDetailFragment"
        private var styledPlayerViewMap:  MutableMap<Int, StyledPlayerView>  = mutableMapOf()
        private var thumbnailImageViewMap:  MutableMap<Int, ImageView>  = mutableMapOf()
        private var currentStyledPlayerView: Pair<Int, StyledPlayerView>? = null
        private var currentPlayingIndex: Int = -1

        fun resetCurrentPlayingIndex() {
            currentPlayingIndex = -1
        }

        // call when item recycled to improve performance
        fun releaseRecycledPlayers(index: Int){
            styledPlayerViewMap.remove(index)
            thumbnailImageViewMap.remove(index)
        }

        // call when scroll to pause any playing player
        fun pauseCurrentPlayingVideo(){
            if (currentStyledPlayerView != null) {
                currentStyledPlayerView?.second?.player = null
            }
        }

        fun playIndexThenPausePreviousPlayer(index: Int, mediaItem: MediaItem, thePlayer: ThePlayer){
            if (!currentPlayingIndex.equals(index)) {
                // swap video content
                pauseCurrentPlayingVideo()
                currentPlayingIndex = index
                currentStyledPlayerView = Pair(index, styledPlayerViewMap.get(index)!!)
                val thumbnailView = thumbnailImageViewMap.get(index)
                thePlayer.setMediaUri(mediaItem.uri, thumbnailView!!)
                currentStyledPlayerView!!.second.player = thePlayer.getPlayer()
            }
        }

        fun getCurrentVideoPlayingIndex(): Int {
            return currentPlayingIndex
        }

        @JvmStatic
        @BindingAdapter(value = ["video_uri", "display_name", "on_state_change", "thumbnail", "item_index", "autoPlay"], requireAll = false)
        fun StyledPlayerView.loadVideo(uri: Uri, displayName: String, callback: PlayerStateCallback, thumbnail: ImageView, item_index: Int? = null, autoPlay: Boolean = false) {
            // When changing track, retain the latest frame instead of showing a black screen
            setKeepContentOnPlayerReset(true)
            this.useController = false
            this.keepScreenOn = true

            if (item_index != null) {
                styledPlayerViewMap[item_index] = this
                thumbnailImageViewMap[item_index] = thumbnail
            }
        }
    }
}