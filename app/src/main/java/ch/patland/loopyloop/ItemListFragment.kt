package ch.patland.loopyloop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ch.patland.loopyloop.databinding.FragmentItemListBinding
import ch.patland.loopyloop.databinding.ItemListContentBinding
import ch.patland.loopyloop.media.MediaCursor
import ch.patland.loopyloop.media.MediaDirectory

class ItemListFragment : Fragment() {
    private var _binding: FragmentItemListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.itemList

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)
        setupRecyclerView(recyclerView, itemDetailFragmentContainer)
    }

    private fun setupRecyclerView(
            recyclerView: RecyclerView,
            itemDetailFragmentContainer: View?
    ) {
        val mediaCursor = MediaCursor(requireContext())
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(
            mediaCursor.findAllDirs(),
            itemDetailFragmentContainer
        )
    }

    class SimpleItemRecyclerViewAdapter(
            private val values: List<MediaDirectory>,
            private val itemDetailFragmentContainer: View?
    ) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {
        private val TAG = "SimpleItemRecyclerViewAdapter"
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val binding = ItemListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.contentView.text = item.directory

            with(holder.itemView) {
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
            }
        }

        override fun getItemCount(): Int {
            return values.size
        }

        override fun getItemId(position: Int): Long {
            val mediaDirectory = values.get(position)
            return mediaDirectory.directoryId.toLong()
        }

        fun getItem(position: Int): Any {
            return values.get(position)
        }

        inner class ViewHolder(binding: ItemListContentBinding) : RecyclerView.ViewHolder(binding.root) {
            val contentView: TextView = binding.content
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}