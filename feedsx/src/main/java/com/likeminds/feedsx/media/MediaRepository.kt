package com.likeminds.feedsx.media

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.*
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import com.annimon.stream.Stream
import com.likeminds.feedsx.R
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.utils.DateUtil
import com.likeminds.feedsx.utils.ValueUtils.getMediaType
import com.likeminds.feedsx.utils.ValueUtils.getOrDefault
import com.likeminds.feedsx.utils.file.isLargeFile
import com.likeminds.feedsx.utils.model.ITEM_MEDIA_PICKER_DOCUMENT
import java.util.*
import javax.inject.Inject

/**
 * Handles the retrieval of media present on the user's device.
 */

@SuppressLint("InlinedApi")
class MediaRepository @Inject constructor() {

    companion object {
        private const val CAMERA = "Camera"
        private const val ALL_MEDIA_BUCKET_ID = "com.collabmates.ALL_MEDIA"
    }

    /**
     * Retrieves a list of folders that contain media.
     */
    fun getLocalFolders(
        context: Context, mediaTypes: List<String>,
        callback: (folders: List<MediaFolderViewData>) -> Unit,
    ) {
        callback(getFolders(context, mediaTypes))
    }

    /**
     * Retrieves a list of media items (images and videos) that are present in the specified bucket.
     */
    fun getMediaInBucket(
        context: Context, bucketId: String, mediaTypes: List<String>,
        callback: (medias: List<MediaViewData>) -> Unit,
    ) {
        callback(getMediaInBucket(context, bucketId, mediaTypes))
    }

    /**
     * Retrieves a list of all the document files from local storage.
     */
    fun getLocalDocumentFiles(context: Context, callback: (medias: List<MediaViewData>) -> Unit) {
        callback(getAllDocumentFiles(context))
    }

    /**
     * Retrieves basic details of list of shared Uris from local storage.
     */
    fun getLocalUrisDetails(
        context: Context, contentUris: List<Uri>, callback: (medias: List<MediaViewData>) -> Unit,
    ) {
        callback(getUriDetails(context, contentUris))
    }

    @WorkerThread
    private fun getFolders(context: Context, mediaTypes: List<String>): List<MediaFolderViewData> {
        val mediaFolders = ArrayList<MediaFolderViewData>()
        var data: Triple<MutableMap<String, FolderData>, String?, Uri?>? = null

        when {
            MediaType.isBothImageAndVideo(mediaTypes) -> {
                val imageFolders = getFolders(context, Images.Media.EXTERNAL_CONTENT_URI)
                val videoFolders = getFolders(context, Video.Media.EXTERNAL_CONTENT_URI)
                data = getFoldersMap(imageFolders, videoFolders)
            }
            MediaType.isImage(mediaTypes) -> {
                val imageFolders = getFolders(context, Images.Media.EXTERNAL_CONTENT_URI)
                data = getFoldersMap(imageFolders, null)
            }
            MediaType.isVideo(mediaTypes) -> {
                val videoFolders = getFolders(context, Video.Media.EXTERNAL_CONTENT_URI)
                data = getFoldersMap(null, videoFolders)
            }
        }

        if (data != null) {
            val cameraFolder = if (data.second != null) data.first.remove(data.second) else null
            mediaFolders.addAll(
                Stream.of(data.first.values).map { folder ->
                    val folderType =
                        MediaFolderType.NORMAL
                    MediaFolderViewData.Builder()
                        .thumbnailUri(folder.thumbnail)
                        .title(folder.getFolderTitle())
                        .itemCount(folder.count)
                        .bucketId(folder.bucketId)
                        .folderType(folderType).build()
                }.toList().sortedWith(
                    compareBy { it.title }
                )
            )

            if (data.third != null) {
                var allMediaCount = Stream.of(mediaFolders).reduce(
                    0
                ) { count: Int, folder: MediaFolderViewData -> count + folder.itemCount } ?: 0
                if (cameraFolder != null) {
                    allMediaCount += cameraFolder.count
                }
                mediaFolders.add(
                    0, MediaFolderViewData.Builder()
                        .thumbnailUri(data.third)
                        .title(context.getString(R.string.all_media))
                        .itemCount(allMediaCount)
                        .bucketId(ALL_MEDIA_BUCKET_ID)
                        .folderType(MediaFolderType.NORMAL)
                        .build()
                )
            }
            if (cameraFolder != null) {
                mediaFolders.add(
                    0, MediaFolderViewData.Builder()
                        .thumbnailUri(cameraFolder.thumbnail)
                        .title(cameraFolder.getFolderTitle())
                        .itemCount(cameraFolder.count)
                        .bucketId(cameraFolder.bucketId)
                        .folderType(MediaFolderType.CAMERA)
                        .build()
                )
            }
        }
        return mediaFolders
    }

    /**
     * @return Triple<A,B,C> where
     * A denotes All available folders map
     * B denotes Camera bucket id if there is any
     * C denotes AllMedia bucket Thumbnail Uri if there is any
     * */
    private fun getFoldersMap(
        imageFolders: FolderResult?,
        videoFolders: FolderResult?,
    ): Triple<MutableMap<String, FolderData>, String?, Uri?> {
        val folders: MutableMap<String, FolderData> = HashMap()
        if (imageFolders != null) {
            updateFoldersMap(imageFolders, folders)
        }

        if (videoFolders != null) {
            updateFoldersMap(videoFolders, folders)
        }

        val cameraBucketId = imageFolders?.cameraBucketId ?: videoFolders?.cameraBucketId
        val allMediaThumbnail =
            if ((imageFolders?.thumbnailTimestamp ?: 0) > (videoFolders?.thumbnailTimestamp ?: 0)) {
                imageFolders?.thumbnail
            } else {
                videoFolders?.thumbnail
            }
        return Triple(folders, cameraBucketId, allMediaThumbnail)
    }

    private fun updateFoldersMap(
        folderResult: FolderResult,
        folders: MutableMap<String, FolderData>,
    ) {
        for ((key, value) in folderResult.folderData) {
            if (folders.containsKey(key)) {
                folders[key]?.incrementCount(value.count)
            } else {
                folders[key] = value
            }
        }
    }

    @WorkerThread
    private fun getFolders(context: Context, contentUri: Uri): FolderResult {
        var cameraBucketId: String? = null
        var globalThumbnail: Uri? = null
        var thumbnailTimestamp: Long = 0
        val folders: MutableMap<String, FolderData> = HashMap()
        val projection = arrayOf(
            Images.Media._ID,
            Images.Media.BUCKET_ID,
            Images.Media.BUCKET_DISPLAY_NAME,
            Images.Media.DATE_MODIFIED
        )
        val selection = isNotPending
        val sortBy =
            Images.Media.BUCKET_DISPLAY_NAME + " COLLATE NOCASE ASC, " + Images.Media.DATE_MODIFIED + " DESC"
        context.contentResolver.query(contentUri, projection, selection, null, sortBy)
            .use { cursor ->
                while (cursor != null && cursor.moveToNext()) {
                    val rowId = cursor.getLong(cursor.getColumnIndexOrThrow(projection[0]))
                    val thumbnail = ContentUris.withAppendedId(contentUri, rowId)
                    val bucketId = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(projection[2]))
                    val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(projection[3]))
                    val folder = getOrDefault(
                        folders, bucketId,
                        FolderData(thumbnail, title, bucketId)
                    )
                    if (folder != null) {
                        folder.incrementCount()
                        folders[bucketId] = folder
                        if (cameraBucketId == null && CAMERA == title) {
                            cameraBucketId = bucketId
                        }
                        if (timestamp > thumbnailTimestamp) {
                            globalThumbnail = thumbnail
                            thumbnailTimestamp = timestamp
                        }
                    }
                }
            }
        return FolderResult(cameraBucketId, globalThumbnail, thumbnailTimestamp, folders)
    }

    @WorkerThread
    private fun getMediaInBucket(
        context: Context,
        bucketId: String,
        mediaTypes: List<String>,
    ): List<MediaViewData> {
        val media: MutableList<MediaViewData> = ArrayList()
        when {
            MediaType.isBothImageAndVideo(mediaTypes) -> {
                media.addAll(
                    getMediaInBucket(context, bucketId, Images.Media.EXTERNAL_CONTENT_URI, true)
                )
                media.addAll(
                    getMediaInBucket(context, bucketId, Video.Media.EXTERNAL_CONTENT_URI, false)
                )
            }
            MediaType.isImage(mediaTypes) -> {
                media.addAll(
                    getMediaInBucket(context, bucketId, Images.Media.EXTERNAL_CONTENT_URI, true)
                )
            }
            MediaType.isVideo(mediaTypes) -> {
                media.addAll(
                    getMediaInBucket(context, bucketId, Video.Media.EXTERNAL_CONTENT_URI, false)
                )
            }
        }
        return media.sortedByDescending { it.date }
    }

    @WorkerThread
    private fun getMediaInBucket(
        context: Context, bucketId: String, contentUri: Uri, isImage: Boolean,
    ): List<MediaViewData> {
        val media: MutableList<MediaViewData> = LinkedList()
        var selection = Images.Media.BUCKET_ID + " = ? AND " + isNotPending
        var selectionArgs: Array<String>? = arrayOf(bucketId)
        val sortBy = Images.Media.DATE_MODIFIED + " DESC"
        val projection: Array<String> = if (isImage) {
            arrayOf(
                Images.Media._ID,
                Images.Media.MIME_TYPE,
                Images.Media.DATE_MODIFIED,
                Images.Media.ORIENTATION,
                Images.Media.WIDTH,
                Images.Media.HEIGHT,
                Images.Media.SIZE,
                Images.Media.DISPLAY_NAME
            )
        } else {
            arrayOf(
                Video.Media._ID,
                Video.Media.MIME_TYPE,
                Video.Media.DATE_MODIFIED,
                Video.Media.WIDTH,
                Video.Media.HEIGHT,
                Video.Media.SIZE,
                Video.Media.DURATION,
                Video.Media.DISPLAY_NAME
            )
        }
        if (ALL_MEDIA_BUCKET_ID == bucketId) {
            selection = isNotPending
            selectionArgs = null
        }
        context.contentResolver.query(contentUri, projection, selection, selectionArgs, sortBy)
            .use { cursor ->
                try {
                    while (cursor != null && cursor.moveToNext()) {
                        val rowId = cursor.getLong(cursor.getColumnIndexOrThrow(projection[0]))
                        val uri = ContentUris.withAppendedId(contentUri, rowId)
                        val mimetype =
                            cursor.getString(cursor.getColumnIndexOrThrow(Images.Media.MIME_TYPE))
                        val date =
                            cursor.getLong(cursor.getColumnIndexOrThrow(Images.Media.DATE_MODIFIED))
                        val size = cursor.getLong(cursor.getColumnIndexOrThrow(Images.Media.SIZE))

                        val duration = if (!isImage) {
                            val d = cursor.getColumnIndexOrThrow(Video.Media.DURATION)
                            if (!cursor.isNull(d) && d >= 0) {
                                cursor.getInt(d) / 1000
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                        val mediaName =
                            cursor.getString(cursor.getColumnIndexOrThrow(Files.FileColumns.DISPLAY_NAME))
                        val mediaType = if (isImage) IMAGE else VIDEO
                        media.add(
                            MediaViewData.Builder().uri(uri)
                                .mimeType(mimetype)
                                .mediaType(mediaType)
                                .date(date)
                                .size(size)
                                .duration(duration)
                                .bucketId(bucketId)
                                .dateTimeStampHeader(DateUtil.getDateTitleForGallery(date))
                                .mediaName(mediaName)
                                .build()
                        )
                    }
                } catch (e: Exception) {
                    e.localizedMessage?.let { Log.e("SDK", it) }
                }
            }
        return media
    }

    @WorkerThread
    private fun getAllDocumentFiles(context: Context): List<MediaViewData> {
        val supportedMimeTypes = arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"))
        return getAllDocumentFiles(context, supportedMimeTypes)
    }

    @WorkerThread
    private fun getAllDocumentFiles(
        context: Context,
        mimeTypes: Array<String?>,
    ): List<MediaViewData> {
        val contentUri = Files.getContentUri(VOLUME_EXTERNAL)
        val media: MutableList<MediaViewData> = LinkedList()
        val sortBy = Files.FileColumns.DATE_MODIFIED + " DESC"
        val projection = arrayOf(
            Files.FileColumns._ID,
            Files.FileColumns.MIME_TYPE,
            Files.FileColumns.DATE_MODIFIED,
            Files.FileColumns.SIZE,
            Files.FileColumns.DISPLAY_NAME,
            Files.FileColumns.TITLE
        )
        val selection = Files.FileColumns.MIME_TYPE + "=? AND " + isNotPending
        context.contentResolver.query(contentUri, projection, selection, mimeTypes, sortBy)
            .use { cursor ->
                while (cursor != null && cursor.moveToNext()) {
                    val rowId = cursor.getLong(cursor.getColumnIndexOrThrow(projection[0]))
                    val uri = ContentUris.withAppendedId(contentUri, rowId)
                    val mimetype =
                        cursor.getString(cursor.getColumnIndexOrThrow(Files.FileColumns.MIME_TYPE))
                    val date =
                        cursor.getLong(cursor.getColumnIndexOrThrow(Files.FileColumns.DATE_MODIFIED))
                    val size =
                        cursor.getLong(cursor.getColumnIndexOrThrow(Files.FileColumns.SIZE))
                    var mediaName =
                        cursor.getString(cursor.getColumnIndexOrThrow(Files.FileColumns.DISPLAY_NAME))
                    if (mediaName == null)
                        mediaName =
                            cursor.getString(cursor.getColumnIndexOrThrow(Files.FileColumns.TITLE))
                    if (!size.isLargeFile) {
                        media.add(
                            MediaViewData.Builder()
                                .uri(uri)
                                .mimeType(mimetype)
                                .mediaType(PDF)
                                .date(date)
                                .size(size)
                                .dateTimeStampHeader(DateUtil.getDateTitleForGallery(date))
                                .mediaName(mediaName)
                                .dynamicViewType(ITEM_MEDIA_PICKER_DOCUMENT)
                                .pdfPageCount(getPdfPageCount(context, uri, mimetype))
                                .build()
                        )
                    }
                }
            }
        return media
    }

    @WorkerThread
    private fun getUriDetails(context: Context, contentUris: List<Uri>): List<MediaViewData> {
        val media: MutableList<MediaViewData> = LinkedList()
        contentUris.forEach { contentUri ->
            val mediaViewData = getUriDetail(context, contentUri)
            if (mediaViewData != null) media.add(mediaViewData)
        }
        return media
    }

    @WorkerThread
    private fun getUriDetail(context: Context, contentUri: Uri): MediaViewData? {
        var media: MediaViewData? = null
        context.contentResolver.query(contentUri, null, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToNext()) {
                val mimetype =
                    cursor.getString(cursor.getColumnIndexOrThrow(Files.FileColumns.MIME_TYPE))
                val size =
                    cursor.getLong(cursor.getColumnIndexOrThrow(Files.FileColumns.SIZE))
                val mediaName =
                    cursor.getString(cursor.getColumnIndexOrThrow(Files.FileColumns.DISPLAY_NAME))
                val mediaType = mimetype.getMediaType() ?: contentUri.getMediaType(context)
                ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(mediaName).getMediaType()
                val duration =
                    if (MediaType.isVideo(mediaType)) {
                        if (!cursor.isNull(cursor.getColumnIndexOrThrow(Files.FileColumns.DURATION))) {
                            cursor.getInt(cursor.getColumnIndexOrThrow(Files.FileColumns.DURATION)) / 1000
                        } else {
                            null
                        }
                    } else {
                        null
                    }

                if (mediaType != null) {
                    media = MediaViewData.Builder()
                        .uri(contentUri)
                        .mimeType(mimetype)
                        .mediaType(mediaType)
                        .size(size)
                        .mediaName(mediaName)
                        .duration(duration)
                        .pdfPageCount(getPdfPageCount(context, contentUri, mimetype))
                        .build()
                }
            }
        }
        return media
    }

    private fun getPdfPageCount(context: Context, uri: Uri, mimeType: String?): Int? {
        if (MediaUtils.isPdfType(mimeType)) {
            try {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                if (parcelFileDescriptor != null) {
                    val renderer = PdfRenderer(parcelFileDescriptor)
                    parcelFileDescriptor.close()
                    return renderer.pageCount
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    private val isNotPending: String
        get() = if (Build.VERSION.SDK_INT <= 28) {
            Images.Media._ID + " NOT NULL"
        } else {
            MediaColumns.IS_PENDING + " != 1"
        }

    private class FolderResult(
        val cameraBucketId: String?,
        val thumbnail: Uri?,
        val thumbnailTimestamp: Long,
        val folderData: Map<String, FolderData>,
    )

    class FolderData(val thumbnail: Uri, private val title: String?, val bucketId: String) {
        var count = 0
            private set

        @JvmOverloads
        fun incrementCount(amount: Int = 1) {
            count += amount
        }

        fun getFolderTitle(): String {
            return title ?: ""
        }
    }
}