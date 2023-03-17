package com.likeminds.feedsx.feed.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.google.gson.Gson
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.file.FileUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor() : ViewModel() {

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
                .userId(userId)
                .userName(userName)
                .isGuest(guest)
                .build()

            _initiateUserResponse.postValue(lmFeedClient.initiateUser(request))
        }
    }

    fun addPost(
        context: Context,
        postTextContent: String?,
        fileUris: List<SingleUriData>?
    ) {
        viewModelScope.launch {
            if (fileUris != null) {
                val updatedFileUris = includeAttachmentMetaData(context, fileUris)
//                startMediaUploadWorker(context, updatedFileUris)
            } else {
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
            when (it.fileType) {
                IMAGE -> {
                    val dimensions = FileUtil.getImageDimensions(context, it.uri)
                    it.toBuilder().width(dimensions.first).height(dimensions.second).build()
                }
                VIDEO -> {
                    val thumbnailUri = FileUtil.getVideoThumbnailUri(context, it.uri)
                    if (thumbnailUri != null) {
                        it.toBuilder().thumbnailUri(thumbnailUri).build()
                    } else {
                        it
                    }
                }
                else -> it
            }
        }
    }

//    private fun startMediaUploadWorker(
//        context: Context,
//        attachments: List<SingleUriData>
//    ): Pair<WorkContinuation, String> {
//        val jsonAttachment = Gson().toJson(attachments)
//        val oneTimeWorkRequest =
//            PostAttachmentUploadWorker.getInstance(jsonAttachment)
//        val workContinuation =
//            WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
//        collabmatesSDK.postPreferences.setAttachmentUploadWorkerUUID(oneTimeWorkRequest.id.toString())
//        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
//    }
}