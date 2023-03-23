package com.likeminds.feedsx.feed.viewmodel

import android.content.Context
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.Gson
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.post.create.util.PostAttachmentUploadWorker
import com.likeminds.feedsx.posttypes.model.LinkOGTags
import com.likeminds.feedsx.utils.UserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter.convertAttachments
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.file.FileUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserResponse
import com.likeminds.likemindsfeed.post.model.AddPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _initiateUserResponse = MutableLiveData<LMResponse<InitiateUserResponse>>()
    val initiateUserResponse: LiveData<LMResponse<InitiateUserResponse>> = _initiateUserResponse

    val workerState = MediatorLiveData<WorkInfo>()

    private val _addPostResponse = MutableLiveData<Boolean>()
    val addPostResponse: LiveData<Boolean> = _addPostResponse

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    // calls InitiateUser API and posts the response in LiveData
    fun initiateUser(
        apiKey: String,
        userId: String,
        userName: String? = null,
        guest: Boolean
    ) {
        viewModelScope.launchIO {
            val request = InitiateUserRequest.Builder()
                .apiKey(apiKey)
                .deviceId(userPreferences.getDeviceId())
                .userId(userId)
                .userName(userName)
                .isGuest(guest)
                .build()

            val response = lmFeedClient.initiateUser(request)
            val user = response.data?.user
            val memberId = user?.id ?: -1

            // store member_id in prefs
            userPreferences.saveMemberId(memberId)

            _initiateUserResponse.postValue(response)
        }
    }

    // calls AddPost API and posts the response in LiveData
    fun addPost(
        context: Context,
        postTextContent: String?,
        fileUris: List<SingleUriData>?,
        ogTags: LinkOGTags?
    ) {
        viewModelScope.launch {
            if (fileUris != null) {
                // if the post has upload-able attachments
                val updatedFileUris = includeAttachmentMetaData(context, fileUris)
                val worker = startMediaUploadWorker(context, updatedFileUris)
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
                    .text(postTextContent)
                if (ogTags != null) {
                    // if the post has ogTags
                    requestBuilder.attachments(convertAttachments(ogTags))
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