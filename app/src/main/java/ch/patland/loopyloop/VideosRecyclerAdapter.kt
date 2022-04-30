package ch.patland.loopyloop

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.patland.loopyloop.databinding.ItemDetailContentBinding
import ch.patland.loopyloop.media.MediaItem
import ch.patland.loopyloop.player.ThePlayer
import ch.patland.loopyloop.utils.PlayerStateCallback
import ch.patland.loopyloop.utils.PlayerViewAdapter.Companion.releaseRecycledPlayers
import com.google.android.exoplayer2.Player

class VideosRecyclerAdapter(
    private val mContext: Context)
   : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PlayerStateCallback {

    private var mItemClickListener: OnItemClickListener? = null
    private val mAppPrefs: AppPrefs = AppPrefs()
    private var modelList: List<MediaItem> = emptyList()
    private var mPlayer: ThePlayer = ThePlayer(mContext)

    fun updateList(modelList: List<MediaItem>) {
        this.modelList = modelList
        notifyDataSetChanged()
    }

    fun getPlayer(): ThePlayer {
        return mPlayer
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VideoPlayerViewHolder {
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
        val position = holder.absoluteAdapterPosition
        releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    fun getItem(position: Int): MediaItem {
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
            // handle on item click
            binding.root.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    absoluteAdapterPosition,
                    model
                )
            }
            // binding with item_detail_content.xml
            binding.apply {
                dataModel = model
                callback = this@VideosRecyclerAdapter
                index = absoluteAdapterPosition
                executePendingBindings()
            }
        }
    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {}

    override fun onVideoBuffering(player: Player) {}

    override fun onStartedPlaying(player: Player) {}

    override fun onFinishedPlaying(player: Player) {}

    override fun isMuted(): Boolean {
        return mAppPrefs.getMuteChecked(mContext)
    }
}