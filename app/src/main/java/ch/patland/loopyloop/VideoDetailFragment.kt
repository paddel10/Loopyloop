package ch.patland.loopyloop

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ch.patland.loopyloop.databinding.FragmentVideoDetailBinding
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.RepeatModeUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class VideoDetailFragment : Fragment() {
    private var _binding: FragmentVideoDetailBinding? = null

    private lateinit var uri: Uri
    private var isMute: Boolean = false
    private var currentPosition: Long = 0
    private lateinit var mPlayer: ExoPlayer

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setFullscreen()

        arguments?.let {
            if (it.containsKey(ARG_URI)) {
                // Load the placeholder content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                uri = Uri.parse(it.getString(ARG_URI))
                // item = PlaceholderContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
            }
            if (it.containsKey(ARG_IS_MUTE)) {
                isMute = it.getBoolean(ARG_IS_MUTE)
            }
            if (it.containsKey(ARG_CURRENT_POSITION)) {
                currentPosition = it.getLong(ARG_CURRENT_POSITION)
            }
        }

        _binding = FragmentVideoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initPlayer(uri: Uri) {
        val builder = DefaultLoadControl.Builder()
        builder.setBufferDurationsMs(1000, 1000, 1000, 1000)

        mPlayer = ExoPlayer.Builder(requireContext()).setLoadControl(builder.build()).build()
        mPlayer.playWhenReady = true
        mPlayer.repeatMode = Player.REPEAT_MODE_ALL

        if (isMute) {
            mPlayer.volume = 0f
        } else {
            mPlayer.volume = 1f
        }

        val dataSourceFactory = DefaultDataSource.Factory(requireContext())
        // Create a progressive media source pointing to a stream uri.
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
        mPlayer.setMediaSource(mediaSource)
        mPlayer.seekTo(currentPosition)
        mPlayer.prepare()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlayer(uri)
        val playerView = view.findViewById<StyledPlayerView>(R.id.video_detail)
        if (playerView == null) {
            Log.d("detail", "playerView is null")
        }

        playerView?.player = mPlayer
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerView.setRepeatToggleModes(RepeatModeUtil.REPEAT_TOGGLE_MODE_ONE)

        val fab: View = view.findViewById(R.id.fab_close_player)
        fab.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        // activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        super.onPause()
        // activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Clear the systemUiVisibility flag
        // activity?.window?.decorView?.systemUiVisibility = 0
    }

    override fun onDestroy() {
        mPlayer.release()
        super.onDestroy()
    }

    companion object {
        const val ARG_URI = "uri"
        const val ARG_IS_MUTE = "is_mute"
        const val ARG_CURRENT_POSITION = "current_position"
    }

    override fun onDestroyView() {
        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        activity?.window?.decorView?.apply {
            // Calling setSystemUiVisibility() with a value of 0 clears
            // all flags.
            systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        super.onDestroyView()
        _binding = null
    }

    private fun setFullscreen() {
        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        val flags =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
}