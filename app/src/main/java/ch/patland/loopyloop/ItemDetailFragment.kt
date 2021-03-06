package ch.patland.loopyloop

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.patland.loopyloop.VideoDetailFragment.Companion.ARG_CURRENT_POSITION
import ch.patland.loopyloop.VideoDetailFragment.Companion.ARG_IS_MUTE
import ch.patland.loopyloop.VideoDetailFragment.Companion.ARG_URI
import ch.patland.loopyloop.databinding.FragmentItemDetailBinding
import ch.patland.loopyloop.media.MediaCursor
import ch.patland.loopyloop.media.MediaItem
import ch.patland.loopyloop.media.MediaStoreObserver
import ch.patland.loopyloop.media.MediaStoreObserverInterface
import ch.patland.loopyloop.model.MediaItemViewModel
import ch.patland.loopyloop.utils.PlayerViewAdapter.Companion.getCurrentVideoPlayingIndex
import ch.patland.loopyloop.utils.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import ch.patland.loopyloop.utils.PlayerViewAdapter.Companion.resetCurrentPlayingIndex
import ch.patland.loopyloop.utils.RecyclerViewScrollListener

class ItemDetailFragment : Fragment(), MediaStoreObserverInterface {
    private val TAG = "ItemDetailFragment"
    private lateinit var mAdapter: VideosRecyclerAdapter
    private lateinit var mMediaItemViewModel: MediaItemViewModel
    private var mAppPrefs: AppPrefs = AppPrefs()

    private var directoryId: String? = null
    private var directory: String? = null

    var mMediaStoreObserver: MediaStoreObserver? = null

    private var _binding: FragmentItemDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                directoryId = it.getString(ARG_ITEM_ID)
                directory = it.getString(ARG_ITEM_DIRECTORY)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        updateContent()

        setHasOptionsMenu(true)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.video_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        val isChecked: Boolean = mAppPrefs.getMuteChecked(requireContext())
        val item = menu.findItem(R.id.action_mute)
        item.isChecked = isChecked
        if (isChecked) {
            mAdapter.getPlayer().turnOffVolume()
            item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_volume_off_24)
        } else {
            mAdapter.getPlayer().turnOnVolume()
            item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_volume_up_24)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_mute) {
            item.isChecked = !item.isChecked
            mAppPrefs.setMuteChecked(requireContext(), item.isChecked)
            if (item.isChecked) {
                mAdapter.getPlayer().turnOffVolume()
                item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_volume_off_24)
            } else {
                mAdapter.getPlayer().turnOnVolume()
                item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_volume_up_24)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.itemDetail
        recyclerView.setHasFixedSize(true)

        val scrollListener = object : RecyclerViewScrollListener() {
            // Called when end of scroll is reached.
            override fun onItemIsFirstVisibleItem(index: Int) {
                // play just visible item
                if (index != -1) {
                    val mediaItem = mAdapter.getItem(index)
                    playIndexThenPausePreviousPlayer(index, mediaItem, mAdapter.getPlayer())
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)

        val mediaCursor = MediaCursor(requireContext())
        val values = mediaCursor.findItemsByBucketId(directoryId!!)

        mMediaItemViewModel = ViewModelProvider(this).get(MediaItemViewModel::class.java)

        mAdapter = VideosRecyclerAdapter(requireActivity())

        mMediaItemViewModel.mediaItemsLiveData.observe(viewLifecycleOwner, Observer { mediaItems ->
            //update files in adapter
            mAdapter.updateList(mediaItems)
        })
        mMediaItemViewModel.postMediaItems(values)

        mAdapter.SetOnItemClickListener(object : VideosRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int, model: MediaItem?) {
                resetCurrentPlayingIndex()
                val bundle = getBundleForVideoDetail(model?.uri, mAdapter.getPlayer().getPlayer().currentPosition)
                view?.findNavController()?.navigate(R.id.action_item_detail_fragment_to_videoDetailFragment, bundle)
            }
        })
        // use a linear layout manager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mAdapter

        registerMediaStoreObserver()

        // swipe to delete
        /*val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.absoluteAdapterPosition
                val mediaItem: MediaItem = mAdapter.getItem(pos)
                // mMediaViewModel.delete(mediaItem)
                val fileUtil = FileUtil(requireContext())
                val uri = mediaItem.uri
                val path = fileUtil.getFilePath(uri)
                val fdelete = path?.let { File(it) }
                if (fdelete!!.exists()) {
                    if (fdelete.delete()) {
                        Log.d("delete", "delete file successful!")
                        // mAdapter.notifyItemRemoved(pos)
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)*/
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val playingIndex = getCurrentVideoPlayingIndex()
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && playingIndex != 1) {
            // orientation is landscape and video is playing - change to video detail fragment
            val mediaItem: MediaItem = mAdapter.getItem(playingIndex)
            val bundle = getBundleForVideoDetail(mediaItem.uri, mAdapter.getPlayer().getPlayer().currentPosition)
            view?.findNavController()?.navigate(R.id.action_item_detail_fragment_to_videoDetailFragment, bundle)
        }
    }

    private fun registerMediaStoreObserver() {
        val handler = Handler(Looper.myLooper()!!)
        mMediaStoreObserver = MediaStoreObserver(this, handler)
        requireContext().contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            true,
            mMediaStoreObserver!!
        )
    }

    private fun deregisterMediaStoreObserver() {
        if (mMediaStoreObserver !== null) {
            requireContext().contentResolver.unregisterContentObserver(mMediaStoreObserver!!)
        }
    }

    private fun getBundleForVideoDetail(uri: Uri?, currentPosition: Long): Bundle {
        val bundle = Bundle()
        bundle.putString(ARG_URI, uri.toString())
        bundle.putBoolean(ARG_IS_MUTE, mAppPrefs.getMuteChecked(requireContext()))
        bundle.putLong(ARG_CURRENT_POSITION, currentPosition)
        return bundle
    }

    private fun updateContent() {
        (activity as AppCompatActivity).supportActionBar?.title = directory
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
        const val ARG_ITEM_DIRECTORY = "item_directory"
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        deregisterMediaStoreObserver()
        mAdapter.getPlayer().releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        mAdapter.getPlayer().pausePlayer()
    }

    override fun updateMediaItems() {
        val mediaCursor = MediaCursor(requireContext())
        val values = mediaCursor.findItemsByBucketId(directoryId!!)
        mMediaItemViewModel.postMediaItems(values)
    }
}