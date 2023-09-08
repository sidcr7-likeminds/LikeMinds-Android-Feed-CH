package com.likeminds.feedsx.utils.permissions.util

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.likeminds.feedsx.R
import com.likeminds.feedsx.utils.permissions.model.PermissionExtras

class LMFeedPermission private constructor(
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

        private const val REQUEST_STORAGE = 10101
        private const val REQUEST_GALLERY = 10102

        fun getStoragePermissionData(): LMFeedPermission {
            return LMFeedPermission(
                WRITE_STORAGE,
                REQUEST_STORAGE,
                "To easily receive and send photos, videos and other files, allow LikeMinds access to your device’s photos, media and files.",
                "To send media, allow LikeMinds access to your device’s photos, media and files. Tap on Settings > Permission, and turn Storage on.",
                R.drawable.ic_folder
            )
        }

        // returns the [PermissionExtras] for gallery permissions request
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun getGalleryPermissionExtras(context: Context): PermissionExtras {
            return PermissionExtras.Builder()
                .permissions(
                    arrayOf(
                        READ_MEDIA_VIDEO,
                        READ_MEDIA_IMAGES
                    )
                )
                .requestCode(REQUEST_GALLERY)
                .preDialogMessage(context.getString(R.string.pre_gallery_media_permission_dialog_message))
                .deniedDialogMessage(context.getString(R.string.denied_gallery_media_permission_dialog_message))
                .dialogImage(R.drawable.ic_folder)
                .build()
        }
    }
}
