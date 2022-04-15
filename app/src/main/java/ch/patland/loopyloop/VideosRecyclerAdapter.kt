package ch.patland.loopyloop

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.patland.loopyloop.databinding.ItemDetailContentBinding
import ch.patland.loopyloop.media.MediaItem
import ch.patland.loopyloop.utils.PlayerStateCallback
import ch.patland.loopyloop.utils.PlayerViewAdapter.Companion.releaseRecycledPlayers
import com.google.android.exoplayer2.Player
import java.util.ArrayList

class VideosRecyclerAdapter(
    private val mContext: Context,
    private var modelList: List<MediaItem>)
   : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PlayerStateCallback {

    private var mItemClickListener: OnItemClickListener? = null
    private val mAppPrefs: AppPrefs = AppPrefs()

    fun updateList(modelList: ArrayList<MediaItem>) {
        this.modelList = modelList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VideoPlayerViewHolder {
        /*val binding: FacebookTimelineItemRecyclerListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context)
            , R.layout.item_detail_content, viewGroup, false)*/
        val binding = ItemDetailContentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return VideoPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int) {

        //Here you can fill your row view
        if (holder is VideoPlayerViewHolder) {
            val model = getItem(position)
            val genericViewHolder = holder

            // send data to view holder
            genericViewHolder.onBind(model)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        // If you are calling this in the context of an Adapter, you probably want to call
        // getBindingAdapterPosition() or if you want the position as RecyclerView sees it,
        // you should call getAbsoluteAdapterPosition().
        // holder.absoluteAdapterPosition
        val position = holder.bindingAdapterPosition // holder.adapterPosition
        Log.d("position", "bindingAdapterPosition = " + holder.bindingAdapterPosition.toString())
        Log.d("position", "absoluteAdapterPosition = " + holder.absoluteAdapterPosition.toString())
        Log.d("position", "layoutPosition = " + holder.layoutPosition.toString())
        Log.d("position", "oldPosition = " + holder.oldPosition.toString())
        releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    private fun getItem(position: Int): MediaItem {
        return modelList[position]
    }

    fun SetOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mItemClickListener = mItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            position: Int,
            model: MediaItem?
        )
    }

    inner class VideoPlayerViewHolder(private val binding: ItemDetailContentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: MediaItem) {
            // handel on item click
            binding.root.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    bindingAdapterPosition,//absoluteAdapterPosition,
                    model
                )
            }

            binding.apply {
                dataModel = model
                callback = this@VideosRecyclerAdapter
                index = bindingAdapterPosition // absoluteAdapterPosition
                executePendingBindings()
            }
        }
    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {}

    override fun onVideoBuffering(player: Player) {}

    override fun onStartedPlaying(player: Player) {
        Log.d("playvideo", "start" + player.contentDuration)
    }

    override fun onFinishedPlaying(player: Player) {}

    override fun isMuted(): Boolean {
        return mAppPrefs.getMuteChecked(mContext)
    }
}