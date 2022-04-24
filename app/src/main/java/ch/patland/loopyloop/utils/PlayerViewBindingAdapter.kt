package ch.patland.loopyloop.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource

// extension function for show toast
fun Context.toast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class PlayerViewAdapter {

    companion object{
        // for hold all players generated
        private var playersMap: MutableMap<Int, ExoPlayer>  = mutableMapOf()
        // for hold current player
        private var currentPlayingVideo: Pair<Int, ExoPlayer>? = null
        private var currentPlayingIndex: Int = -1
        fun releaseAllPlayers(){
            playersMap.map {
                it.value.release()
            }
        }

        // call when item recycled to improve performance
        fun releaseRecycledPlayers(index: Int){
            playersMap[index]?.release()
        }

        fun turnOffVolume() {
            playersMap.map {
                it.value.volume = 0f
            }
        }

        fun turnOnVolume() {
            playersMap.map {
                it.value.volume = 1f
            }
        }

        fun getCurrentPosition(index: Int): Long {
            if (playersMap[index] !== null) {
                return playersMap[index]!!.currentPosition
            }
            return 0
        }

        // call when scroll to pause any playing player
        fun pauseCurrentPlayingVideo(){
            if (currentPlayingVideo != null){
                currentPlayingVideo?.second?.playWhenReady = false
            }
        }

        fun playIndexThenPausePreviousPlayer(index: Int){
            if (playersMap.get(index)?.playWhenReady == false) {
                pauseCurrentPlayingVideo()
                playersMap.get(index)?.playWhenReady = true
                currentPlayingVideo = Pair(index, playersMap.get(index)!!)
            }
        }

        /*
        *  url is a url of stream video
        *  progressbar for show when start buffering stream
        * thumbnail for show before video start
        * */
        @JvmStatic
        @BindingAdapter(value = ["video_uri", "display_name", "on_state_change", "thumbnail", "item_index", "autoPlay"], requireAll = false)
        fun StyledPlayerView.loadVideo(uri: Uri, displayName: String, callback: PlayerStateCallback, thumbnail: ImageView, item_index: Int? = null, autoPlay: Boolean = false) {
            Log.d("PlayerViewAdapter", "loadVideo() " + uri.toString() + ", index = " + item_index.toString() + ", autoplay = " + autoPlay.toString())
            val builder = DefaultLoadControl.Builder()
            builder.setBufferDurationsMs(1000, 1000, 1000, 1000)

            val player = ExoPlayer.Builder(context).setLoadControl(builder.build()).build() // ExoPlayer.Builder(context).build()
            player.playWhenReady = autoPlay
            player.repeatMode = Player.REPEAT_MODE_ALL
            // When changing track, retain the latest frame instead of showing a black screen
            setKeepContentOnPlayerReset(true)
            // We'll show the controller, change to true if want controllers as pause and start
            this.useController = false

            val dataSourceFactory = DefaultDataSource.Factory(context)
            // Create a progressive media source pointing to a stream uri.
            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
            player.setMediaSource(mediaSource)

            player.prepare()

            this.player = player

            // add player with its index to map
            if (playersMap.containsKey(item_index))
                playersMap.remove(item_index)

            if (item_index != null)
                playersMap[item_index] = player
            Log.d("playersMap", "item_index = " + item_index.toString() + ", playerMap.size = " + playersMap.size.toString())
            if (callback.isMuted()) {
                this.player!!.volume = 0f
            } else {
                this.player!!.volume = 1f
            }
            this.player!!.addListener(object : Player.Listener {

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    this@loadVideo.context.toast("Oops! Error occurred while playing media.")
                    Log.d("PlayerViewAdapter", "Error while playing: " + error.message + ", oode = " + error.errorCodeName)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    Log.d("PlayerViewAdapter", "onPlayerStateChanged() " + playbackState.toString())
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_BUFFERING) {
                        callback.onVideoBuffering(player)
                        // Buffering..
                        // set progress bar visible here
                        // set thumbnail visible
                        thumbnail.visibility = View.VISIBLE
                    }

                    if (playbackState == Player.STATE_READY) {
                        // set thumbnail gone
                        thumbnail.visibility = View.GONE
                        callback.onVideoDurationRetrieved(
                            this@loadVideo.player!!.duration,
                            player
                        )
                    }

                    if (playbackState == Player.STATE_READY && player.playWhenReady) {
                        // [PlayerView] has started playing/resumed the video
                        callback.onStartedPlaying(player)
                    }
                }
            })
        }
    }
}