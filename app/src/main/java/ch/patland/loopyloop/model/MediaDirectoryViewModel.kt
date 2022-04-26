package ch.patland.loopyloop.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.patland.loopyloop.media.MediaDirectory

class MediaDirectoryViewModel: ViewModel() {
    private val mutableMediaDirectories: MutableLiveData<List<MediaDirectory>> = MutableLiveData()
    val mediaDirectoriesLiveData: LiveData<List<MediaDirectory>> get() = mutableMediaDirectories

    fun postMediaDirectories(mediaDirectories: List<MediaDirectory>) {
        mutableMediaDirectories.postValue(mediaDirectories)
    }
}