package ch.patland.loopyloop.utils

import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import java.io.File


class FileUtil(val context: Context) {
    private val contentResolver by lazy {
        context.contentResolver
    }

    fun getLastModified(uri: Uri): Long {
        val filePath = getFilePath(uri)
        if (filePath != null) {
            val f = File(filePath)
            return f.lastModified()
        }
        return 0
    }

    fun getFilePath(uri: Uri): String?
    {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(uri)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun delete(launcher: ActivityResultLauncher<IntentSenderRequest?>, uri: Uri) {
        val contentResolver = context.contentResolver
        try {

            //delete object using resolver
            contentResolver.delete(uri, null, null)
        } catch (e: SecurityException) {
            var pendingIntent: PendingIntent? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val collection: ArrayList<Uri> = ArrayList()
                collection.add(uri)
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                //if exception is recoverable then again send delete request using intent
                if (e is RecoverableSecurityException) {
                    pendingIntent = e.userAction.actionIntent
                }
            }
            if (pendingIntent != null) {
                val sender = pendingIntent.intentSender
                val request = IntentSenderRequest.Builder(sender).build()
                launcher.launch(request)
            }
        }
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