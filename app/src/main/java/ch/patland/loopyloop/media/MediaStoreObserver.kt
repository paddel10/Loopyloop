package ch.patland.loopyloop.media

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.util.Log

class MediaStoreObserver(handler: Handler): ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange, null)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        // todo: do an update
        Log.d("MediaStoreObserver", "there is a change: " + uri?.path)
    }
}