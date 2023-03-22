package com.likeminds.feedsx.feed.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.utils.UserPreferences
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.google.gson.Gson
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.post.create.util.PostAttachmentUploadWorker
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _initiateUserResponse = MutableLiveData<LMResponse<InitiateUserResponse>>()
    val initiateUserResponse: LiveData<LMResponse<InitiateUserResponse>> = _initiateUserResponse

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
        fileUris: List<SingleUriData>?
    ) {
        viewModelScope.launch {
            val uploadData: Pair<WorkContinuation, String>
            if (fileUris != null) {
                // if the post has upload-able attachments
                val updatedFileUris = includeAttachmentMetaData(context, fileUris)
                uploadData = startMediaUploadWorker(context, updatedFileUris)
                uploadData.first.enqueue()
            } else {
                // if the post does not have any upload-able attachments
                // TODO: call add post api
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
            // generates filename from localFilePath
            val name = FileUtils.getFileNameFromPath(localFilePath)
            // generates awsFolderPath to upload the file
            val awsFolderPath = FileUtil.generateAWSFolderPathFromFileName(name)
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
    @SuppressLint("EnqueueWork")
    private fun startMediaUploadWorker(
        context: Context,
        attachments: List<SingleUriData>
    ): Pair<WorkContinuation, String> {
        val jsonAttachment = Gson().toJson(attachments)
        val oneTimeWorkRequest = PostAttachmentUploadWorker.getInstance(jsonAttachment)
        val workContinuation =
            WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
    }
}