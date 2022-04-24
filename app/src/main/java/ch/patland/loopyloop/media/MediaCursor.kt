package ch.patland.loopyloop.media

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MediaCursor (val context: Context) {
    var idColumn = -1
    var dateTakenColumn = -1
    var dateAddedColumn = -1
    var dateModifiedColumn = -1
    var displayNameColumn = -1
    var bucketIdColumn = -1
    var bucketDisplayNameColumn = -1
    var durationColumn = -1
    var sizeColumn = -1

    fun initIndex(cursor: Cursor) {
        idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)
        dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_TAKEN)
        dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED)
        dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_MODIFIED)
        displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME)
        bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_ID)
        bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME)
        durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)
        sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE)
    }

    private val contentResolver by lazy {
        context.contentResolver
    }

    fun findItemsByBucketId(bucketId: String): List<MediaItem> {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        val collection = MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
        val projection = arrayOf(
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns.DATE_ADDED,
            MediaStore.Video.VideoColumns.DATE_MODIFIED,
            MediaStore.Video.VideoColumns.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
        )
        val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)

        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        val cursor = contentResolver.query(
            // val query = ContentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        val list : ArrayList<MediaItem> = ArrayList()

        cursor?.use { cursor ->
            initIndex(cursor)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                val dateModified = cursor.getLong(dateModifiedColumn)
                val uri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val duration: Long = cursor.getLong(durationColumn)
                val size: Long = cursor.getLong(sizeColumn)
                val lastModified = getLastModified(uri)
                val mediaItem = MediaItem(id, displayName, dateTaken, dateAdded, dateModified, lastModified, duration, size, uri)
                list.add(mediaItem)
            }
        }
        cursor?.close()

        return list.sortedWith(compareBy { it.dateModified }).reversed()
    }

    fun findAllDirs(): List<MediaDirectory> {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        val collection = MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
        val projection = arrayOf(
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns.DATE_ADDED,
            MediaStore.Video.VideoColumns.DATE_MODIFIED,
            MediaStore.Video.VideoColumns.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
        )
        val selection = null
        val selectionArgs = null

        val sortOrder = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} ASC"
        val cursor = contentResolver.query(
            // val query = ContentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        /*
        cursor?.also {
            if (cursor.moveToFirst()) {
                initIndex(cursor)
            }
        }*/
        val hashMap : HashMap<String, MediaDirectory> = HashMap()

        cursor?.use { cursor ->
            initIndex(cursor)
            while (cursor.moveToNext()) {
                val bucketDisplayName = cursor.getString(bucketDisplayNameColumn)
                val bucketId = cursor.getString(bucketIdColumn)
                val mediaDirectory = MediaDirectory(bucketDisplayName, bucketId)
                if (!hashMap.containsKey(bucketId)) {
                    hashMap.put(bucketId, mediaDirectory)
                }
            }
        }
        cursor?.close()

        return ArrayList(hashMap.values).sortedWith(compareBy({it.directory}))
    }

    private fun getLastModified(uri: Uri): Long {
        val filePath = getFilePath(uri)
        if (filePath != null) {
            val f = File(filePath)
            return f.lastModified()
        }
        return 0
    }

    private fun getFilePath(uri: Uri): String?
    {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(uri)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(uri: Uri?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = contentResolver.query(uri!!, projection, null, null,
                null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: java.lang.Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }
}