package com.likeminds.feedsx.post.create.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.Gson
import com.likeminds.feedsx.media.MediaRepository
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.MediaViewData
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.post.PostRepository
import com.likeminds.feedsx.post.create.util.PostAttachmentUploadWorker
import com.likeminds.feedsx.posttypes.model.LinkOGTags
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.file.FileUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.DecodeUrlRequest
import com.likeminds.likemindsfeed.helper.model.DecodeUrlResponse
import com.likeminds.likemindsfeed.post.model.AddPostRequest
import com.likeminds.likemindsfeed.sdk.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _decodeUrlResponse = MutableLiveData<LinkOGTags>()
    val decodeUrlResponse: LiveData<LinkOGTags> = _decodeUrlResponse

    val workerState = MediatorLiveData<WorkInfo>()

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _addPostResponse = MutableLiveData<Boolean>()
    val addPostResponse: LiveData<Boolean> = _addPostResponse

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
            _decodeUrlResponse.postValue(ViewDataConverter.convertOGTags(ogTags))
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
        ogTags: LinkOGTags? = null
    ) {
        viewModelScope.launchIO {
            var updatedText = postTextContent?.trim()
            if (updatedText.isNullOrEmpty()) {
                updatedText = null
            }
            if (fileUris != null) {
                // if the post has upload-able attachments
                val updatedFileUris = includeAttachmentMetaData(context, fileUris)
                val worker = startMediaUploadWorker(context, updatedFileUris)

                // adds post data in local db
                addPost()

                worker.enqueue()
                workerState.apply {
                    addSource(worker.workInfosLiveData) { workInfoList ->
                        val workInfo = workInfoList.firstOrNull {
                            it.tags.contains(PostAttachmentUploadWorker.TAG)
                        }

                        if (workInfo != null) {
                            value = workInfo
                        }
                    }
                }
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
                    _addPostResponse.postValue(true)
                } else {
                    _errorMessage.postValue(response.errorMessage)
                }
            }
        }
    }

    //add post:{} into local db
    private fun addPost(user: User?) {
        if (user == null) return
        viewModelScope.launchIO {
            // convert post into postEntity
            val postEntity = ViewDataConverter.convertUser(user)
            // add it to local db
            postRepository.insertPost(postEntity)
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
                    builder.width(dimensions.first).height(dimensions.second).build()
                }
                VIDEO -> {
                    val thumbnailUri = FileUtil.getVideoThumbnailUri(context, it.uri)
                    if (thumbnailUri != null) {
                        builder.thumbnailUri(thumbnailUri).build()
                    } else {
                        builder.build()
                    }
                }
                else -> builder.build()
            }
        }
    }

    // creates PostAttachmentUploadWorker to start media upload
    private fun startMediaUploadWorker(
        context: Context,
        attachments: List<SingleUriData>
    ): WorkContinuation {
        val jsonAttachment = Gson().toJson(attachments)
        val oneTimeWorkRequest = PostAttachmentUploadWorker.getInstance(jsonAttachment)
        return WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
    }
}