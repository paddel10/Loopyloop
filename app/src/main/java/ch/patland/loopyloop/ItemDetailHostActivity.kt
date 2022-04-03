package ch.patland.loopyloop

import android.Manifest
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import ch.patland.loopyloop.databinding.ActivityItemDetailBinding
import com.vmadalin.easypermissions.EasyPermissions

class ItemDetailHostActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private val TAG = "ItemDetailHostActivity"
    private val RC_STORAGE = 100
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate() called")
        val binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askForPermission()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_item_detail) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun askForPermission() {
        if (EasyPermissions.hasPermissions(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.i(TAG, "askForPermission() permissions granted")
        } else {
            Log.i(TAG, "askForPermission() EasyPermissions.requestPermissions")
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.storage),
                RC_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.i(TAG, "onPermissionsDenied() called")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i(TAG,"onPermissionsGranted() called")
        // bla()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionsResult() called")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * https://developer.android.com/training/data-storage/shared/media
     */
    private fun bla() {
        Log.d(TAG, "bla() called")
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        val collection = MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
                //MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        //MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.RELATIVE_PATH,
            MediaStore.Video.Media.VOLUME_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_ID
        )

// Show only videos that are at least 5 minutes in duration.
        // val selection = "${MediaStore.Video.Media.DURATION} >= ?"
        // val selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"
        /*val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
        )*/
        // val selectionArgs = arrayOf("Download%")
        // val selection = "1=1) group by (" + MediaStore.Video.VideoColumns.BUCKET_ID + ")"// MediaStore.Video.VideoColumns.BUCKET_ID + " IS NOT NULL) GROUP BY (" + MediaStore.Video.VideoColumns.BUCKET_ID
        val selection = null
        val selectionArgs = null

// Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        val query = applicationContext.contentResolver.query(
            // val query = ContentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
            val volumeNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.VOLUME_NAME)
            val bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val hashMap : HashMap<String, String> = HashMap()
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val relativePath = cursor.getString(relativePathColumn)
                val data = cursor.getString(dataColumn)
                val volumeName = cursor.getString(volumeNameColumn)
                val bucketDisplayName = cursor.getString(bucketDisplayNameColumn)
                val bucketId = cursor.getString(bucketIdColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                Log.d(TAG, relativePath)
                Log.d(TAG, data)
                Log.d(TAG, bucketDisplayName)
                Log.d(TAG, bucketId)

                if (!hashMap.containsKey(bucketId)) {
                    hashMap.put(bucketId, bucketDisplayName)
                }
                // Stores column values and the contentUri in a local object
                // that represents the media file.
                // videoList += Video(contentUri, name, duration, size)
            }
            for(key in hashMap.keys){
                Log.d(TAG, "$key : ${hashMap[key]}")
            }
        }
    }
}