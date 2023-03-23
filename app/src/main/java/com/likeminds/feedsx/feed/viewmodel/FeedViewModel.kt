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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.feed.UserRepository
import com.likeminds.feedsx.utils.UserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter.convertAttachments
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.file.FileUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.RegisterDeviceRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserResponse
import com.likeminds.likemindsfeed.sdk.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _initiateUserResponse = MutableLiveData<LMResponse<InitiateUserResponse>>()
    val initiateUserResponse: LiveData<LMResponse<InitiateUserResponse>> = _initiateUserResponse

    /***
     * calls InitiateUser API
     * store user:{} in db
     * and posts the response in LiveData
     * */
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

            //call api
            val initiateResponse = lmFeedClient.initiateUser(request)

            if (initiateResponse.success) {
                val user = initiateResponse.data?.user
                val id = user?.id ?: -1

                //add user in local db
                addUser(user)

                //save user.id in local prefs
                userPreferences.saveMemberId(id)
            }
            //send response to UI
            _initiateUserResponse.postValue(initiateResponse)
        }
    }

    //add user:{} into local db
    private fun addUser(user: User?) {
        if (user == null) return
        viewModelScope.launchIO {
            //convert user into userEntity
            val userEntity = ViewDataConverter.convertUser(user)
            //add it to local db
            userRepository.insertUser(userEntity)

            //call member state api
            getMemberState()

            //call register device api
            registerDevice()
        }
    }

    private fun getMemberState() {
        viewModelScope.launchIO {
            //get member state response
            val memberStateResponse = lmFeedClient.getMemberState().data

            val memberState = memberStateResponse?.state ?: return@launchIO
            val isOwner = memberStateResponse.isOwner
            val userId = memberStateResponse.id

            //get existing userEntity
            var userEntity = userRepository.getUser(userId)

            //updated userEntity
            userEntity = userEntity.toBuilder().state(memberState).isOwner(isOwner).build()

            //update userEntity in local db
            userRepository.updateUser(userEntity)
        }
    }

    private fun registerDevice() {
        viewModelScope.launchIO {
            //create request
            val request = RegisterDeviceRequest.Builder()
                .deviceId(userPreferences.getDeviceId())
                .token("YUYUYUYUYUY") //todo fix it with proper token
                .build()

            //call api
            lmFeedClient.registerDevice(request)
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