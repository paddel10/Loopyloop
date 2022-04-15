package ch.patland.loopyloop

import android.content.ClipData
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.patland.loopyloop.databinding.FragmentItemDetailBinding

import ch.patland.loopyloop.media.MediaCursor
import ch.patland.loopyloop.media.MediaItem
import ch.patland.loopyloop.placeholder.PlaceholderContent
import ch.patland.loopyloop.utils.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import ch.patland.loopyloop.utils.PlayerViewAdapter.Companion.releaseAllPlayers
import ch.patland.loopyloop.utils.RecyclerViewScrollListener

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment() {
    private val TAG = "ItemDetailFragment"
    private var mAdapter: VideosRecyclerAdapter? = null

    /**
     * The placeholder content this fragment is presenting.
     */
    private var item: PlaceholderContent.PlaceholderItem? = null
    // private lateinit var mediaItemList: List<MediaItem>;

    private var directoryId: String? = null
    private var directory: String? = null

    // lateinit var itemDetailTextView: TextView
    // private var toolbarLayout: CollapsingToolbarLayout? = null

    private var _binding: FragmentItemDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dragListener = View.OnDragListener { v, event ->
        if (event.action == DragEvent.ACTION_DROP) {
            val clipDataItem: ClipData.Item = event.clipData.getItemAt(0)
            val dragData = clipDataItem.text
            item = PlaceholderContent.ITEM_MAP[dragData]
            updateContent()
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the placeholder content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                directoryId = it.getString(ARG_ITEM_ID)
                directory = it.getString(ARG_ITEM_DIRECTORY)
                // item = PlaceholderContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // toolbarLayout = binding.toolbarLayout
        //itemDetailTextView = binding.itemDetail
        updateContent()
        rootView.setOnDragListener(dragListener)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.itemDetail
        recyclerView.setHasFixedSize(true)

        val scrollListener = object : RecyclerViewScrollListener() {
            // Called when end of scroll is reached.
            override fun onItemIsFirstVisibleItem(index: Int) {
                Log.d("visible item index", index.toString())
                // play just visible item
                if (index != -1)
                    playIndexThenPausePreviousPlayer(index)
            }
        }
        recyclerView.addOnScrollListener(scrollListener)

        val mediaCursor = MediaCursor(requireContext())
        val values = mediaCursor.findItemsByBucketId(directoryId!!)
        mAdapter = VideosRecyclerAdapter(requireActivity(), values)

        mAdapter!!.SetOnItemClickListener(object : VideosRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int, model: MediaItem?) {

            }
        })
        // use a linear layout manager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mAdapter
    }

    private fun updateContent() {
        // toolbarLayout?.title = directory
        (activity as AppCompatActivity).supportActionBar?.title = directory

        // Show the placeholder content as text in a TextView.
        /*item?.let {
            itemDetailTextView.text = "bla"//it.details
        }*/
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        const val ARG_ITEM_DIRECTORY = "item_directory"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        releaseAllPlayers()
    }
}