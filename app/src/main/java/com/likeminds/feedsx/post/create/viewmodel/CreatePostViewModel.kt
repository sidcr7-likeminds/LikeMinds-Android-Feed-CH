package com.likeminds.feedsx.post.create.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.google.gson.Gson
import com.likeminds.feedsx.media.MediaRepository
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.MediaViewData
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.post.PostRepository
import com.likeminds.feedsx.post.create.util.PostAttachmentUploadWorker
import com.likeminds.feedsx.posttypes.model.LinkOGTagsViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.ViewDataConverter.convertAttachment
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.file.FileUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.DecodeUrlRequest
import com.likeminds.likemindsfeed.helper.model.DecodeUrlResponse
import com.likeminds.likemindsfeed.post.model.AddPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _decodeUrlResponse = MutableLiveData<LinkOGTagsViewData>()
    val decodeUrlResponse: LiveData<LinkOGTagsViewData> = _decodeUrlResponse

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _postAdded = MutableLiveData<Boolean>()
    val postAdded: LiveData<Boolean> = _postAdded

    fun fetchUriDetails(
        context: Context,
        uris: List<Uri>,
        callback: (media: List<MediaViewData>) -> Unit,
    ) {
        mediaRepository.getLocalUrisDetails(context, uris, callback)
    }

    fun decodeUrl(url: String) {
        viewModelScope.launchIO {
            val request = DecodeUrlRequest.Builder().url(url).build()

            val response = lmFeedClient.decodeUrl(request)
            postDecodeUrlResponse(response)
        }
    }

    private fun postDecodeUrlResponse(response: LMResponse<DecodeUrlResponse>) {
        if (response.success) {
            // processes link og tags if API call was successful
            val data = response.data ?: return
            val ogTags = data.ogTags
            _decodeUrlResponse.postValue(ViewDataConverter.convertLinkOGTags(ogTags))
        } else {
            // posts error message if API call failed
            _errorMessage.postValue(response.errorMessage)
        }
    }

    // calls AddPost API and posts the response in LiveData
    fun addPost(
        context: Context,
        postTextContent: String?,
        fileUris: List<SingleUriData>? = null,
        ogTags: LinkOGTagsViewData? = null
    ) {
        viewModelScope.launchIO {
            var updatedText = postTextContent?.trim()
            if (updatedText.isNullOrEmpty()) {
                updatedText = null
            }
            if (fileUris != null) {
                // if the post has upload-able attachments
                val updatedFileUris = includeAttachmentMetaData(context, fileUris)
                val uploadData = startMediaUploadWorker(context, updatedFileUris)

                // adds post data in local db
                storePost(
                    uploadData.second,
                    updatedText,
                    updatedFileUris
                )
                uploadData.first.enqueue()
            } else {
                // if the post does not have any upload-able attachments
                val requestBuilder = AddPostRequest.Builder()
                    .text(updatedText)
                if (ogTags != null) {
                    // if the post has ogTags
                    requestBuilder.attachments(ViewDataConverter.convertAttachments(ogTags))
                }
                val request = requestBuilder.build()
                val response = lmFeedClient.addPost(request)
                if (response.success) {
                    _postAdded.postValue(true)
                } else {
                    _errorMessage.postValue(response.errorMessage)
                }
            }
        }
    }

    //add post:{} into local db
    private fun storePost(
        uuid: String,
        text: String?,
        fileUris: List<SingleUriData>? = null
    ) {
        viewModelScope.launchIO {
            if (fileUris == null) {
                return@launchIO
            }
            val temporaryPostId = System.currentTimeMillis()
            val thumbnailUri = fileUris.first().thumbnailUri
            val postEntity = ViewDataConverter.convertPost(
                temporaryPostId,
                uuid,
                thumbnailUri.toString(),
                text
            )
            val attachments = fileUris.map {
                convertAttachment(
                    temporaryPostId,
                    it
                )
            }
            // add it to local db
            postRepository.insertPostWithAttachments(postEntity, attachments)
            _postAdded.postValue(true)
        }
    }

    /**
     * Includes attachment's meta data such as dimensions, thumbnails, etc
     * @param context
     * @param files List<SingleUriData>?
     */
    private fun includeAttachmentMetaData(
        context: Context,
        files: List<SingleUriData>,
    ): List<SingleUriData> {
        return files.map {
            // generates localFilePath from the ContentUri provided by client
            val localFilePath =
                FileUtil.getRealPath(context, it.uri)
            // generates awsFolderPath to upload the file
            val awsFolderPath = FileUtil.generateAWSFolderPathFromFileName(it.mediaName)
            val builder = it.toBuilder().localFilePath(localFilePath)
                .awsFolderPath(awsFolderPath)
            when (it.fileType) {
                IMAGE -> {
                    val dimensions = FileUtil.getImageDimensions(context, it.uri)
                    builder.width(dimensions.first)
                        .thumbnailUri(it.uri)
                        .height(dimensions.second)
                        .build()
                }
                VIDEO -> {
                    val thumbnailUri = FileUtil.getVideoThumbnailUri(context, it.uri)
                    if (thumbnailUri != null) {
                        builder.thumbnailUri(thumbnailUri).build()
                    } else {
                        builder.build()
                    }
                }
                else -> {
                    val thumbnailUri = MediaUtils.getDocumentPreview(context, it.uri)
                    val format = FileUtil.getFileExtensionFromFileName(it.mediaName)
                    builder
                        .thumbnailUri(thumbnailUri)
                        .format(format)
                        .build()
                }
            }
        }
    }

    // creates PostAttachmentUploadWorker to start media upload
    @SuppressLint("EnqueueWork")
    private fun startMediaUploadWorker(
        context: Context,
        attachments: List<SingleUriData>
    ): Pair<WorkContinuation, String> {
        val jsonAttachment = Gson().toJson(attachments)
        val oneTimeWorkRequest = PostAttachmentUploadWorker.getInstance(jsonAttachment)
        val workContinuation = WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
    }
}