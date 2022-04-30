package ch.patland.loopyloop.player

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import ch.patland.loopyloop.utils.toast
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class TheListener(private val mContext: Context, private val mExoPlayer: ExoPlayer): Player.Listener {

    private var mThumbnailView: ImageView? = null

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        mContext.toast("Oops! Error occurred while playing media.")
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        if (playbackState == Player.STATE_BUFFERING && mThumbnailView != null) {
            mThumbnailView!!.visibility = View.VISIBLE
        }
        if (playbackState == Player.STATE_READY && mThumbnailView != null) {
            mThumbnailView!!.visibility = View.GONE
        }
    }

    fun setThumbnailView(thumbnailView: ImageView) {
        mThumbnailView = thumbnailView
    }
}