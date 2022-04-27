package ch.patland.loopyloop.media

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler

class MediaStoreObserver(private val mediaStoreObserverInterface: MediaStoreObserverInterface,
                         handler: Handler): ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange, null)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        // do an update
        mediaStoreObserverInterface.updateMediaItems()
    }
}