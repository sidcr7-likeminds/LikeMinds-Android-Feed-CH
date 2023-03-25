package com.likeminds.feedsx.utils.file

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.Size
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.likeminds.feedsx.utils.file.PathUtils.getPath
import java.io.*

object FileUtil {

    private const val TAG = "FileUtil"

    /**
     * returns the package of file provider, required for attachments
     **/
    fun getFileProviderPackage(context: Context): String {
        return "${context.packageName}.fileprovider"
    }

    /**
     * Returns sd card path for an Uri
     * @param uri single Uri
     */
    fun getRealPath(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val pathTempFile = getFullPathTemp(context, uri)
        val file = File(pathTempFile)
        val returnedPath = getPath(context, uri)
        return when {
            //Cloud
            uri.isCloudFile -> {
                downloadFile(contentResolver, file, uri)
                pathTempFile
            }
            //Third Party App
            returnedPath.isBlank() -> {
                downloadFile(contentResolver, file, uri)
                pathTempFile
            }
            //Unknown Provider or unknown mime type
            uri.isUnknownProvider(returnedPath, contentResolver) -> {
                downloadFile(contentResolver, file, uri)
                pathTempFile
            }
            //LocalFile
            else -> {
                returnedPath
            }
        }
    }

    /**
     * Returns subfolder from the main folder to the file location or empty string
     * EXAMPLE:
     * Input uriString = "content://com.android.providers.downloads.documents/document/raw%3%2Fstorage%2Femulated%2F0%2FDownload%2FsubFolder%2FsubFolder2%2Ffile.jpg"
     * Input folderRoot = "Download"
     * Output: subFolder/subFolder2/
     *
     * @param uriString Path file
     * @param folderRoot It is usually "Download"
     */
    fun getSubFolders(uriString: String, folderRoot: String = Constants.PathUri.FOLDER_DOWNLOAD) =
        uriString
            .replace("%2F", "/")
            .replace("%20", " ")
            .replace("%3A", ":")
            .split("/")
            .run {
                val indexRoot = indexOf(folderRoot)
                if (folderRoot.isNotBlank().and(indexRoot != -1)) {
                    subList(indexRoot + 1, lastIndex)
                        .joinToString(separator = "") { "$it/" }
                } else {
                    ""
                }
            }

    @JvmStatic
    fun getUriFromBitmapWithRandomName(
        context: Context,
        bitmap: Bitmap?,
        shareUriExternally: Boolean = false,
        isPNGFormat: Boolean = false
    ): Uri? {
        if (bitmap == null) {
            return null
        }
        val imagesFolder = File(context.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "${System.currentTimeMillis()}.png")

            val stream = FileOutputStream(file)
            val compressFormat =
                if (isPNGFormat) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            bitmap.compress(compressFormat, 100, stream)
            stream.flush()
            stream.close()
            uri = if (!shareUriExternally) {
                Uri.fromFile(file)
            } else {
                FileProvider.getUriForFile(
                    context,
                    getFileProviderPackage(context),
                    file
                )
            }
        } catch (e: IOException) {
            Log.e(
                TAG,
                "IOException while trying to write file for sharing: " + e.localizedMessage
            )
        }
        return uri
    }

    private fun getBitmapFromUri(uri: Uri?, context: Context): Bitmap? {
        var bitmap: Bitmap? = null
        uri?.let {
            try {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")!!
                val fileDescriptor = parcelFileDescriptor.fileDescriptor
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
            } catch (e: IOException) {
                Log.e(
                    "FileUtils",
                    "IOException while trying to get bitmap from uri: " + e.localizedMessage
                )
            }
        }
        return bitmap
    }

    fun getSharedImageUri(context: Context, uri: Uri?): Uri? {
        if (uri == null) {
            return null
        }
        return try {
            val oldExifOrientation = ExifInterface(getRealPath(context, uri))
                .getAttribute(ExifInterface.TAG_ORIENTATION)
            val bitmap = getBitmapFromUri(uri, context) ?: return null
            val newUri = getUriFromBitmapWithRandomName(context, bitmap) ?: return null
            if (oldExifOrientation != null) {
                val newExif = ExifInterface(getRealPath(context, newUri))
                newExif.setAttribute(ExifInterface.TAG_ORIENTATION, oldExifOrientation)
                newExif.saveAttributes()
            }
            newUri
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "getSharedImageUri", e)
            null
        }
    }

    fun getVideoThumbnailUri(context: Context, videoUri: Uri?): Uri? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoUri.toString(), HashMap())
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever?.release()
        }
        if (bitmap == null && videoUri != null) {
            val path = getRealPath(context, videoUri)
            if (path.isEmpty()) {
                return null
            }
            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ThumbnailUtils.createVideoThumbnail(
                    File(path), Size(600, 600), CancellationSignal()
                )
            } else {
                ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND)
            }
        }
        return getUriFromBitmapWithRandomName(context, bitmap)
    }

    fun getImageDimensions(context: Context, uri: Uri): Pair<Int, Int> {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")!!
            val fileDescriptor = parcelFileDescriptor.fileDescriptor
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            parcelFileDescriptor.close()
            Pair(options.outWidth, options.outHeight)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Pair(0, 0)
        }
    }

    fun getSharedPdfUri(context: Context, oldUri: Uri?): Uri? {
        if (oldUri == null) {
            return null
        }
        var newUri: Uri? = null
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(oldUri, "r")!!
            val fileDescriptor = parcelFileDescriptor.fileDescriptor

            val pdfsFolder = File(context.cacheDir, "pdfs")
            pdfsFolder.mkdirs()
            val file = File(pdfsFolder, "${System.currentTimeMillis()}.pdf")

            val inputStream: InputStream = FileInputStream(fileDescriptor)
            val outputStream = FileOutputStream(file)

            // Transfer bytes from in to out
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            newUri = FileProvider.getUriForFile(
                context,
                getFileProviderPackage(context),
                file
            )
        } catch (e: IOException) {
            Log.e(
                "FileUtils",
                "IOException while trying to copy pdf from uri: " + e.localizedMessage
            )
        }
        return newUri
    }

    fun getSharedVideoUri(context: Context, oldUri: Uri?): Uri? {
        var newUri: Uri? = null
        oldUri?.let {
            try {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(oldUri, "r")!!
                val fileDescriptor = parcelFileDescriptor.fileDescriptor

                val videosFolder = File(context.cacheDir, "videos")
                videosFolder.mkdirs()
                val file = File(videosFolder, "${System.currentTimeMillis()}.mp4")

                val inputStream: InputStream = FileInputStream(fileDescriptor)
                val outputStream = FileOutputStream(file)

                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    outputStream.write(buf, 0, len)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
                newUri = Uri.fromFile(file)
            } catch (e: IOException) {
                Log.e(
                    "FileUtils",
                    "IOException while trying to copy video from uri: " + e.localizedMessage
                )
            }
        }
        return newUri
    }

    private fun getFullPathTemp(context: Context, uri: Uri): String {
        val folder: File? = context.getExternalFilesDir("Temp")
        return "${folder.toString()}/${getFileName(context, uri)}"
    }

    fun getFileName(context: Context?, fileUri: Uri): String? {
        var fileName: String? = null
        if (fileUri.scheme == ContentResolver.SCHEME_CONTENT) {
            context?.contentResolver?.query(fileUri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                fileName = cursor.getString(nameIndex)
            }
        } else if (fileUri.scheme == ContentResolver.SCHEME_FILE) {
            fileName = File(fileUri.path.toString()).name
        } else {
            fileName = fileUri.path
            val cut = fileName?.lastIndexOf('/') ?: -1
            if (cut != -1) fileName = fileName?.substring(cut.plus(1))
        }
        return fileName
    }

    /**
     *  Method that downloads the file to an internal folder at the root of the project.
     *  For cases where the file has an unknown provider, cloud files and for users using
     *  third-party file explorer api.
     *
     * @param uri of the file
     * @return new path string
     */
    fun downloadFile(
        contentResolver: ContentResolver,
        file: File,
        uri: Uri
    ): Boolean {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(1024)
                    var read: Int = input.read(buffer)
                    while (read != -1) {
                        output.write(buffer, 0, read)
                        read = input.read(buffer)
                    }
                }
            }
        } catch (e: Exception) {
            file.deleteRecursively()
            e.printStackTrace()
            Log.e(TAG, "downloadFile", e)
        }
        return true
    }

    /**
     * @param fileName - Name of the file to be uploaded
     * @return awsFolderPath - Generates and returns AWS folder path where file will be uploaded
     */
    fun generateAWSFolderPathFromFileName(
        fileName: String?
    ): String {
        //TODO: use user_unique_id
        val userUniqueId = "6a4cc38e-02c7-4dfa-96b7-68a3078ad922"
        val fileNameWithoutExtension = fileName?.substringBeforeLast(".")
        val extension = getFileExtensionFromFileName(fileName)
        return "post/$userUniqueId/" + fileNameWithoutExtension + "-" + System.currentTimeMillis() + "." + extension
    }

    fun getFileExtensionFromFileName(
        fileName: String?
    ): String? {
        return fileName?.substringAfterLast(".", "")
    }
}

private const val LARGE_FILE_SIZE = 100 //in MegaBytes

val File.size get() = if (!exists()) 0.0 else length().toDouble()

/**
 * Size value should be in bytes
 * */
private val Long.sizeInKb get() = this / 1000
private val Long.sizeInMb get() = sizeInKb / 1000
val Long.isLargeFile get() = sizeInMb > LARGE_FILE_SIZE