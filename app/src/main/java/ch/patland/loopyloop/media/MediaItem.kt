package ch.patland.loopyloop.media

import android.net.Uri
import android.text.format.DateUtils
import java.io.File
import java.text.SimpleDateFormat
import kotlin.time.Duration.Companion.milliseconds

data class MediaItem(
    val id: Long,
    val displayName: String,
    val dateTaken: Long,    // number of milliseconds since 1970-01-01T00:00:00Z.
    val dateAdded: Long,    // use this if dateTaken is 0
    val dateModified: Long,
    val lastModified: Long,
    val duration: Long,     // milliseconds
    val size: Long,         // number of bytes.
    val uri: Uri) {

    fun isSameDisplayName(displayName: String): Boolean {
        return this.displayName == displayName;
    }

    fun formatDateTaken(): String {
        return formatDate(dateTaken)
    }

    fun formatDateAdded(): String {
        return formatDate(dateAdded)
    }

    fun formatDateModified(): String {
        return formatDate(dateModified)
    }

    fun formatLastModified(): String {
        return formatDate(lastModified)
    }

    fun formatFileDateTime(): String {
        return ""
    }

    private fun formatDate(date: Long): String {
        if (date.compareTo(0) == 0) {
            return "n/a"
        }
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        return simpleDateFormat.format(date)
    }

    fun formatDuration(): String {
        return DateUtils.formatElapsedTime(duration / 1000)
    }

    fun formatDurationLong(): String {
        return duration.milliseconds.toString()
    }

    fun formatSize() =
        when {
            size >= 1 shl 30 -> "%.1f GB".format(size.toDouble() / (1 shl 30))
            size >= 1 shl 20 -> "%.1f MB".format(size.toDouble() / (1 shl 20))
            size >= 1 shl 10 -> "%.0f kB".format(size.toDouble() / (1 shl 10))
            else -> "$size bytes"
        }
}
