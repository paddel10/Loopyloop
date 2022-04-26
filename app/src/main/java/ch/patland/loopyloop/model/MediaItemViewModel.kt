package ch.patland.loopyloop.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.patland.loopyloop.media.MediaItem

class MediaItemViewModel: ViewModel() {
    private val mutableMediaItems: MutableLiveData<List<MediaItem>> = MutableLiveData()
    val mediaItemsLiveData: LiveData<List<MediaItem>> get() = mutableMediaItems

    fun postMediaItems(mediaItems: List<MediaItem>) {
        mutableMediaItems.postValue(mediaItems)
    }
}