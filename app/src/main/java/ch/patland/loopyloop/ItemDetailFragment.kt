package ch.patland.loopyloop

import android.content.ClipData
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ch.patland.loopyloop.databinding.FragmentItemDetailBinding
import ch.patland.loopyloop.databinding.ItemDetailContentBinding

import ch.patland.loopyloop.media.MediaCursor
import ch.patland.loopyloop.media.MediaItem
import ch.patland.loopyloop.placeholder.PlaceholderContent
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.ListPreloader.PreloadSizeProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.FixedPreloadSizeProvider
import com.google.android.material.appbar.CollapsingToolbarLayout
import kohii.v1.exoplayer.Kohii
import java.lang.Math.pow
import java.util.*


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment() {
    private val TAG = "ItemDetailFragment"
    private val imageWidthPixels = 1024;
    private val imageHeightPixels = 768;
    /**
     * The placeholder content this fragment is presenting.
     */
    private var item: PlaceholderContent.PlaceholderItem? = null
    private lateinit var mediaItemList: List<MediaItem>;
    private lateinit var kohii: Kohii;

    private var directoryId: String? = null
    private var directory: String? = null

    lateinit var itemDetailTextView: TextView
    private var toolbarLayout: CollapsingToolbarLayout? = null

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
    ): View? {

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

        kohii = Kohii[this]
        kohii.register(this /* Fragment or Activity */).addBucket(recyclerView)
        setupRecyclerView(recyclerView)
    }

    private fun initGlide(): RequestManager {
        val options = RequestOptions()
            .placeholder(R.drawable.picture)
            .fitCenter()
            .error(R.drawable.picture)
            .frame(3000000)
        return GlideApp.with(this).setDefaultRequestOptions(options)
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView?
    ) {
        // val requestManager = initGlide()
        val mediaCursor = MediaCursor(requireContext())
        val values = mediaCursor.findItemsByBucketId(directoryId!!)

        recyclerView?.adapter = SimpleItemRecyclerViewAdapter(
            values,
            kohii,
            initGlide()
        )
    }

    class SimpleItemRecyclerViewAdapter(
        private val values: List<MediaItem>,
        private val kohii: Kohii,
        private val requestManager: RequestManager
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {
        private val TAG = "SimpleItemRecyclerViewAdapter"
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val binding = ItemDetailContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d(TAG, "onBindViewHolder() called " + position)
            val item = values[position]
            //holder.idView.text = item.id.toString()
            holder.contentView.text = item.displayName
            holder.dateTaken.text = item.formatDateTaken()
            holder.sizeDuration.text = item.formatSize().plus(" / ").plus(item.formatDuration())

            Log.d(TAG, item.formatSize())
            val playerView = holder.itemView.findViewById<FrameLayout>(R.id.media_container)

            val thumbnail = holder.itemView.findViewById<ImageView>(R.id.thumbnail)
            if (thumbnail == null) {
                Log.e(TAG, "thumbnail is null)")
                return
            }

            // adjust start depending on duration - this to avoid black thumbnails
            if (item.duration > 30000) {
                Log.d(TAG, "adjust frame")
                val options: RequestOptions = RequestOptions()
                    .frame(30000000)
                requestManager.applyDefaultRequestOptions(options)
            }
            requestManager
                .load(item.uri)
                .into(thumbnail)


            /*kohii.setUp(item.uri) {
                tag = "${item.uri}+${position}"
            }.bind(playerView)*/

            /*with(holder.itemView) {
                tag = item
                setOnClickListener { itemView ->
                    val item = itemView.tag as MediaDirectory
                    val bundle = Bundle()
                    bundle.putString(
                        ItemDetailFragment.ARG_ITEM_ID,
                        item.directoryId
                    )
                    bundle.putString(
                        ItemDetailFragment.ARG_ITEM_DIRECTORY,
                        item.directory
                    )
                    if (itemDetailFragmentContainer != null) {
                        itemDetailFragmentContainer.findNavController()
                            .navigate(R.id.fragment_item_detail, bundle)
                    } else {
                        itemView.findNavController().navigate(R.id.show_item_detail, bundle)
                    }
                }

                /**
                 * Context click listener to handle Right click events
                 * from mice and trackpad input to provide a more native
                 * experience on larger screen devices
                 */
                setOnContextClickListener { v ->
                    val item = v.tag as PlaceholderContent.PlaceholderItem
                    Toast.makeText(
                        v.context,
                        "Context click of item " + item.id,
                        Toast.LENGTH_LONG
                    ).show()
                    true
                }

                setOnLongClickListener { v ->
                    // Setting the item id as the clip data so that the drop target is able to
                    // identify the id of the content
                    val clipItem = ClipData.Item(item.id)
                    val dragData = ClipData(
                            v.tag as? CharSequence,
                            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                            clipItem
                    )

                    v.startDragAndDrop(
                            dragData,
                            View.DragShadowBuilder(v),
                            null,
                            0
                    )
                }
            }*/
        }

        override fun getItemCount(): Int {
            return values.size
        }

        override fun getItemId(position: Int): Long {
            Log.d(TAG, "getItemId() called")
            val mediaItem = values.get(position)
            return mediaItem.id
        }
        fun getItem(position: Int): Any {
            return values.get(position)
        }
        inner class ViewHolder(binding: ItemDetailContentBinding) : RecyclerView.ViewHolder(binding.root) {
            //val idView: TextView = binding.idText
            val contentView: TextView = binding.idItemName
            val dateTaken: TextView = binding.dateTaken
            val sizeDuration: TextView = binding.sizeDuration

            init {
                Log.d(TAG, "contentView " + contentView.text)
            }
        }
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
}