package com.likeminds.feedsx.utils.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.likeminds.feedsx.R

class Permission private constructor(
    val permissionName: String,
    val requestCode: Int,
    val preDialogMessage: String,
    val deniedDialogMessage: String,
    @param:DrawableRes @field:DrawableRes
    val dialogImage: Int
) {
    companion object {

        private const val WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val READ_MEDIA_VIDEO = Manifest.permission.READ_MEDIA_VIDEO

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO

        private const val REQUEST_STORAGE = 10101
        private const val REQUEST_NOTIFICATIONS = 10102
        private const val REQUEST_GALLERY = 10103
        private const val REQUEST_AUDIO = 10104

        fun getStoragePermissionData(): Permission {
            return Permission(
                WRITE_STORAGE,
                REQUEST_STORAGE,
                "To easily receive and send photos, videos and other files, allow LikeMinds access to your device’s photos, media and files.",
                "To send media, allow LikeMinds access to your device’s photos, media and files. Tap on Settings > Permission, and turn Storage on.",
                R.drawable.ic_folder
            )
        }
    }
}
