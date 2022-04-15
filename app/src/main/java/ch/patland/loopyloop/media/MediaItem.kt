package ch.patland.loopyloop.media

import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import java.text.SimpleDateFormat
import kotlin.time.Duration.Companion.milliseconds

data class MediaItem(
    val id: Long,
    val displayName: String,
    val dateTaken: Long,    // number of milliseconds since 1970-01-01T00:00:00Z.
    val dateAdded: Long,    // use this if dateTaken is 0
    val dateModified: Long,
    val duration: Long,     // milliseconds
    val size: Long,         // number of bytes.
    val uri: Uri) {

    fun isSameDisplayName(displayName: String): Boolean {
        return this.displayName == displayName;
    }

    fun formatDateTaken(): String {
        var date = dateTaken
        Log.d("date", "dateTaken = " + date.toString() + ", dateAdded = " + dateTaken.toString() + ", dateModified = " + dateModified.toString())
        if (date.compareTo(0) == 0) {
            date = dateModified
            Log.d("date", "using date modified: " + date.toString())
        }
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        return simpleDateFormat.format(date)
    }

    fun formatDuration(): String {
        return DateUtils.formatElapsedTime(duration / 1000)
    }

    fun formatSize() =
        when {
            size >= 1 shl 30 -> "%.1f GB".format(size.toDouble() / (1 shl 30))
            size >= 1 shl 20 -> "%.1f MB".format(size.toDouble() / (1 shl 20))
            size >= 1 shl 10 -> "%.0f kB".format(size.toDouble() / (1 shl 10))
            else -> "$size bytes"
        }
}
