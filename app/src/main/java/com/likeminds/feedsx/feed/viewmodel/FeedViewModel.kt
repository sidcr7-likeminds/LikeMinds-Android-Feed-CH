package com.likeminds.feedsx.feed.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.likeminds.feedsx.LMAnalytics
import com.likeminds.feedsx.feed.UserRepository
import com.likeminds.feedsx.post.PostWithAttachmentsRepository
import com.likeminds.feedsx.post.create.util.PostAttachmentUploadWorker
import com.likeminds.feedsx.post.create.util.PostPreferences
import com.likeminds.feedsx.posttypes.model.IMAGE
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.posttypes.model.VIDEO
import com.likeminds.feedsx.utils.UserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.ViewDataConverter.convertPost
import com.likeminds.feedsx.utils.ViewDataConverter.createAttachments
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingDecoder
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS
import com.likeminds.feedsx.utils.model.ITEM_POST_MULTIPLE_MEDIA
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.helper.model.RegisterDeviceRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindsfeed.post.model.*
import com.likeminds.likemindsfeed.sdk.model.User
import com.likeminds.likemindsfeed.universalfeed.model.GetFeedRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postWithAttachmentsRepository: PostWithAttachmentsRepository,
    private val userPreferences: UserPreferences,
    private val postPreferences: PostPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _userResponse = MutableLiveData<UserViewData>()
    val userResponse: LiveData<UserViewData> = _userResponse

    private val _logoutResponse = MutableLiveData<Boolean>()
    val logoutResponse: LiveData<Boolean> = _logoutResponse

    private val _universalFeedResponse = MutableLiveData<Pair<Int, List<PostViewData>>>()
    val universalFeedResponse: LiveData<Pair<Int, List<PostViewData>>> = _universalFeedResponse

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class InitiateUser(val errorMessage: String?) : ErrorMessageEvent()
        data class UniversalFeed(val errorMessage: String?) : ErrorMessageEvent()
        data class AddPost(val errorMessage: String?) : ErrorMessageEvent()
    }

    sealed class PostDataEvent {
        data class PostDbData(val post: PostViewData) : PostDataEvent()

        data class PostResponseData(val post: PostViewData) : PostDataEvent()
    }

    private val postDataEventChannel = Channel<PostDataEvent>(Channel.BUFFERED)
    val postDataEventFlow = postDataEventChannel.receiveAsFlow()

    companion object {
        const val PAGE_SIZE = 20
    }

    /***
     * calls InitiateUser API
     * store user:{} in db
     * and posts the response in LiveData
     * */
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
                val data = initiateResponse.data ?: return@launchIO
                if (data.logoutResponse != null) {
                    //user is invalid
                    _logoutResponse.postValue(true)
                } else {
                    val user = data.user
                    val id = user?.userUniqueId ?: ""

                    //add user in local db
                    addUser(user)

                    //save user.id in local prefs
                    userPreferences.saveUserUniqueId(id)

                    getUniversalFeed(1)

                    //post the user response in LiveData
                    _userResponse.postValue(ViewDataConverter.convertUser(user))
                }
            } else {
                errorMessageChannel.send(ErrorMessageEvent.InitiateUser(initiateResponse.errorMessage))
            }
        }
    }

    //add user:{} into local db
    private fun addUser(user: User?) {
        if (user == null) return
        viewModelScope.launchIO {
            //convert user into userEntity
            val userEntity = ViewDataConverter.createUserEntity(user)
            //add it to local db
            userRepository.insertUser(userEntity)

            //call member state api
            getMemberState()

            //call register device api
            registerDevice()
        }
    }

    //call member state api
    private fun getMemberState() {
        viewModelScope.launchIO {
            //get member state response
            val memberStateResponse = lmFeedClient.getMemberState().data

            val memberState = memberStateResponse?.state ?: return@launchIO
            val isOwner = memberStateResponse.isOwner
            val userId = memberStateResponse.userUniqueId

            //get existing userEntity
            var userEntity = userRepository.getUser(userId)

            //updated userEntity
            userEntity = userEntity.toBuilder().state(memberState).isOwner(isOwner).build()

            //update userEntity in local db
            userRepository.updateUser(userEntity)
        }
    }

    //call register device
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

    //get universal feed
    fun getUniversalFeed(page: Int) {
        viewModelScope.launchIO {
            val request = GetFeedRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)
                .build()

            //call universal feed api
            val response = lmFeedClient.getFeed(request)

            if (response.success) {
                val data = response.data ?: return@launchIO
                val posts = data.posts
                val usersMap = data.users

                //convert to view data
                val listOfPostViewData =
                    ViewDataConverter.convertUniversalFeedPosts(posts, usersMap)

                //send it to ui
                _universalFeedResponse.postValue(Pair(page, listOfPostViewData))
            } else {
                //for error
                errorMessageChannel.send(ErrorMessageEvent.UniversalFeed(response.errorMessage))
            }
        }
    }

    // fetches posts temporary id from prefs
    fun getTemporaryId(): Long {
        return postPreferences.getTemporaryId()
    }

    // calls AddPost API and posts the response in LiveData
    fun addPost(postingData: PostViewData) {
        viewModelScope.launchIO {
            val updatedText =
                if (postingData.text.isNullOrEmpty()) {
                    null
                } else {
                    postingData.text
                }
            val request = AddPostRequest.Builder()
                .text(updatedText)
                .attachments(createAttachments(postingData.attachments))
                .build()

            val response = lmFeedClient.addPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val postViewData = convertPost(
                    data.post,
                    data.users
                )
                sendPostCreationCompletedEvent(postViewData)
                postDataEventChannel.send(
                    PostDataEvent.PostResponseData(postViewData)
                )
                // post added successfully update the post in db
                val temporaryId = postPreferences.getTemporaryId()
                val postId = data.post.id
                postWithAttachmentsRepository.updateIsPosted(
                    temporaryId,
                    postId,
                    true
                )
                postWithAttachmentsRepository.updatePostIdInAttachments(postId, temporaryId)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.AddPost(response.errorMessage))
            }
            postPreferences.saveTemporaryId(-1)
        }
    }

    // fetches pending post data from db
    fun fetchPendingPostFromDB() {
        viewModelScope.launchIO {
            val postWithAttachments = postWithAttachmentsRepository.getLatestPostWithAttachments()
            if (postWithAttachments == null || postWithAttachments.post.isPosted) {
                return@launchIO
            } else {
                val temporaryId = postWithAttachments.post.temporaryId
                postPreferences.saveTemporaryId(temporaryId)
                postDataEventChannel.send(PostDataEvent.PostDbData(convertPost(postWithAttachments)))
            }
        }
    }

    // creates PostAttachmentUploadWorker to start media upload
    @SuppressLint("EnqueueWork")
    private fun startMediaUploadWorker(
        context: Context,
        postId: Long,
        filesCount: Int
    ): Pair<WorkContinuation, String> {
        val oneTimeWorkRequest = PostAttachmentUploadWorker.getInstance(postId, filesCount)
        val workContinuation = WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
    }

    // starts a media upload worker to retry failed uploads
    fun createRetryPostMediaWorker(
        context: Context,
        postId: Long?,
        attachmentCount: Int,
    ) {
        viewModelScope.launchIO {
            if (postId == null || attachmentCount <= 0) {
                return@launchIO
            }
            val uploadData = startMediaUploadWorker(context, postId, attachmentCount)
            postWithAttachmentsRepository.updateUploadWorkerUUID(postId, uploadData.second)
            uploadData.first.enqueue()
            fetchPendingPostFromDB()
        }
    }

    /**
     * Triggers when the user clicks on New Post button
     **/
    fun sendPostCreationStartedEvent() {
        LMAnalytics.track(LMAnalytics.Events.POST_CREATION_STARTED)
    }

    fun sendCommentListOpenEvent() {
        LMAnalytics.track(LMAnalytics.Events.COMMENT_LIST_OPEN)
    }

    private fun sendPostCreationCompletedEvent(
        post: PostViewData
    ) {
        val map = hashMapOf<String, String>()
        val taggedUsers = MemberTaggingDecoder.decodeAndReturnAllTaggedMembers(post.text)
        if (taggedUsers.isNotEmpty()) {
            map["user_tagged"] = "yes"
            map["tagged_users_count"] = taggedUsers.size.toString()
            val taggedUserIds =
                taggedUsers.joinToString {
                    it.first
                }
            map["tagged_users_id"] = taggedUserIds
        } else {
            map["user_tagged"] = "no"
        }
        val attachmentInfo = getEventAttachmentInfo(post)
        attachmentInfo.forEach {
            map[it.first] = it.second.toString()
        }
        LMAnalytics.track(
            LMAnalytics.Events.POST_CREATION_COMPLETED,
            map
        )
    }

    private fun getEventAttachmentInfo(post: PostViewData): List<Pair<String, Int>> {
        return when (post.viewType) {
            ITEM_POST_SINGLE_IMAGE -> {
                listOf(Pair("image_attached", 1))
            }
            ITEM_POST_SINGLE_VIDEO -> {
                listOf(Pair("video_attached", 1))
            }
            ITEM_POST_DOCUMENTS -> {
                listOf(Pair("document_attached", post.attachments.size))
            }
            ITEM_POST_MULTIPLE_MEDIA -> {
                listOf(
                    Pair(
                        "image_attached",
                        post.attachments.filter { it.attachmentType == IMAGE }.size
                    ),
                    Pair(
                        "video_attached",
                        post.attachments.filter { it.attachmentType == VIDEO }.size
                    )
                )
            }
            else -> {
                return emptyList()
            }
        }
    }
}