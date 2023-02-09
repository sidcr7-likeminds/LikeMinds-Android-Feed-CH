package com.likeminds.feedsx.utils.file

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.likeminds.feedsx.utils.file.Constants.PathUri.PATH_DOWNLOAD
import com.likeminds.feedsx.utils.file.Constants.PathUri.PATH_DROPBOX
import com.likeminds.feedsx.utils.file.Constants.PathUri.PATH_EXTERNAL_STORAGE
import com.likeminds.feedsx.utils.file.Constants.PathUri.PATH_GOOGLE_APPS
import com.likeminds.feedsx.utils.file.Constants.PathUri.PATH_GOOGLE_PHOTOS
import com.likeminds.feedsx.utils.file.Constants.PathUri.PATH_MEDIA
import com.likeminds.feedsx.utils.file.Constants.PathUri.PATH_ONEDRIVE
import com.likeminds.feedsx.utils.file.Constants.PathUri.PATH_RAW_DOWNLOAD
import java.util.*

/**
 * Checks Uri authority
 *
 */
internal val Uri.isExternalStorageDocument get() = PATH_EXTERNAL_STORAGE == authority

internal val Uri.isDownloadsDocument get() = PATH_DOWNLOAD == authority

internal val Uri.isMediaDocument get() = PATH_MEDIA == authority

internal val Uri.isGooglePhotosUri get() = PATH_GOOGLE_PHOTOS == authority

internal val Uri.isRawDownloadsDocument get() = toString().contains(PATH_RAW_DOWNLOAD)

internal val Uri.isMediaStore get() = "content".equals(scheme, ignoreCase = true)

internal val Uri.isFile get() = "file".equals(scheme, ignoreCase = true)

/**
 * Check different providers
 *
 */
private val Uri.isDropBox
    get() = toString().lowercase(Locale.ROOT).contains("content://${PATH_DROPBOX}")

private val Uri.isGoogleDrive
    get() = toString().lowercase(Locale.ROOT).contains(PATH_GOOGLE_APPS)

private val Uri.isOneDrive
    get() = toString().lowercase(Locale.ROOT).contains(PATH_ONEDRIVE)

internal val Uri.isCloudFile
    get() = (isOneDrive or isGoogleDrive or isDropBox)

internal fun Uri.isUnknownProvider(
    returnedPath: String,
    contentResolver: ContentResolver
): Boolean {
    val mime = MimeTypeMap.getSingleton()
    val subStringExtension =
        returnedPath.substring(returnedPath.lastIndexOf(".") + 1)
    val extensionFromMime =
        mime.getExtensionFromMimeType(contentResolver.getType(this))
    return scheme.let { subStringExtension != extensionFromMime && it == ContentResolver.SCHEME_CONTENT }
}