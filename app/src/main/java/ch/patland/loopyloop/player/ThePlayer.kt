package ch.patland.loopyloop.player

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.google.android.exoplayer2.*

class ThePlayer(context: Context) {
    private val TAG = "ThePlayer"
    private var mContext: Context
    private var mPlayer: ExoPlayer
    private var mListener: TheListener

    init {
        val builder = DefaultLoadControl.Builder()
        builder.setBufferDurationsMs(2000, 2000, 2000, 2000)

        mContext = context
        mPlayer = ExoPlayer.Builder(context).setLoadControl(builder.build()).build()
        mPlayer.repeatMode = Player.REPEAT_MODE_ALL

        mListener = TheListener(mContext, mPlayer)
    }

    fun setMediaUri(uri: Uri, thumbnailView: ImageView) {
        mPlayer.removeListener(mListener)
        mListener.setThumbnailView(thumbnailView)
        mPlayer.addListener(mListener)
        mPlayer.setMediaItem(MediaItem.fromUri(uri))
        mPlayer.playWhenReady = true
        mPlayer.prepare()
    }

    fun releasePlayer() {
        mPlayer.release()
    }

    fun pausePlayer() {
        mPlayer.pause()
    }

    fun getPlayer(): ExoPlayer {
        return mPlayer
    }

    fun turnOffVolume() {
        mPlayer.volume = 0f
    }

    fun turnOnVolume() {
        mPlayer.volume = 1f
    }
}