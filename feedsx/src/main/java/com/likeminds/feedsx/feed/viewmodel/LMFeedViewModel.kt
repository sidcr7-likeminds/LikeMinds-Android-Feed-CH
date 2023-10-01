package com.likeminds.feedsx.feed.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.post.PostWithAttachmentsRepository
import com.likeminds.feedsx.post.create.util.LMFeedPostPreferences
import com.likeminds.feedsx.post.create.util.PostAttachmentUploadWorker
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.ViewDataConverter.convertPost
import com.likeminds.feedsx.utils.ViewDataConverter.createAttachments
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingDecoder
import com.likeminds.feedsx.utils.model.*
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.post.model.*
import com.likeminds.likemindsfeed.topic.model.GetTopicRequest
import com.likeminds.likemindsfeed.universalfeed.model.GetFeedRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import kotlin.collections.set

class LMFeedViewModel @Inject constructor(
    private val postWithAttachmentsRepository: PostWithAttachmentsRepository,
    private val postPreferences: LMFeedPostPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _universalFeedResponse = MutableLiveData<Pair<Int, List<PostViewData>>>()
    val universalFeedResponse: LiveData<Pair<Int, List<PostViewData>>> = _universalFeedResponse

    private val _unreadNotificationCount = MutableLiveData<Int>()
    val unreadNotificationCount: LiveData<Int> = _unreadNotificationCount

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class UniversalFeed(val errorMessage: String?) : ErrorMessageEvent()
        data class AddPost(val errorMessage: String?) : ErrorMessageEvent()
        data class GetUnreadNotificationCount(val errorMessage: String?) : ErrorMessageEvent()
        data class GetTopic(val errorMessage: String?) : ErrorMessageEvent()
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

    //get universal feed
    fun getUniversalFeed(page: Int, topicsId: List<String>? = null) {
        viewModelScope.launchIO {
            val request = GetFeedRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)
                .topicIds(topicsId)
                .build()

            //call universal feed api
            val response = lmFeedClient.getFeed(request)

            if (response.success) {
                val data = response.data ?: return@launchIO
                val posts = data.posts
                val usersMap = data.users
                val topicsMap = data.topics

                //convert to view data
                val listOfPostViewData =
                    ViewDataConverter.convertUniversalFeedPosts(posts, usersMap, topicsMap)

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

            val topicIds = postingData.topics.map {
                it.id
            }

            val request = AddPostRequest.Builder()
                .text(updatedText)
                .attachments(createAttachments(postingData.attachments))
                .topicIds(topicIds)
                .build()

            val response = lmFeedClient.addPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val postViewData = convertPost(
                    data.post,
                    data.users,
                    data.topics
                )

                // sends post creation completed event
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

    //get unread notification count
    fun getUnreadNotificationCount() {
        viewModelScope.launchIO {
            //call unread notification count api
            val response = lmFeedClient.getUnreadNotificationCount()

            if (response.success) {
                val data = response.data ?: return@launchIO
                val count = data.count

                _unreadNotificationCount.postValue(count)
            } else {
                //for error
                errorMessageChannel.send(ErrorMessageEvent.GetUnreadNotificationCount(response.errorMessage))
            }
        }
    }

    fun getTopicIdsFromAdapterList(items: List<BaseViewType>): List<String> {
        return items.map {
            (it as LMFeedTopicViewData).id
        }
    }

    /**
     * Triggers when the user opens feed fragment
     **/
    fun sendFeedOpenedEvent() {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.FEED_OPENED,
            mapOf(
                "feed_type" to "universal_feed"
            )
        )
    }

    /**
     * Triggers when the user clicks on New Post button
     **/
    fun sendPostCreationStartedEvent() {
        LMFeedAnalytics.track(LMFeedAnalytics.Events.POST_CREATION_STARTED)
    }

    /**
     * Triggers when the user opens post detail screen
     **/
    fun sendCommentListOpenEvent() {
        LMFeedAnalytics.track(LMFeedAnalytics.Events.COMMENT_LIST_OPEN)
    }

    /**
     * Triggers when the user opens post is created successfully
     **/
    private fun sendPostCreationCompletedEvent(
        post: PostViewData
    ) {
        val map = hashMapOf<String, String>()
        // fetches list of tagged users
        val taggedUsers = MemberTaggingDecoder.decodeAndReturnAllTaggedMembers(post.text)
        val topics = post.topics

        // adds tagged user count and their ids in the map
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

        if (topics.isNotEmpty()) {
            val topicsNameString = topics.joinToString(", ") { it.name }
            map["topics_added"] = "yes"
            map["topics"] = topicsNameString
        } else {
            map["topics_added"] = "no"
        }

        // gets event property key and corresponding value for post attachments
        val attachmentInfo = getEventAttachmentInfo(post)
        attachmentInfo.forEach {
            map[it.first] = it.second
        }
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.POST_CREATION_COMPLETED,
            map
        )
    }

    /**
     * @param post - view data of post
     * @return - a list of pair of event key and value
     * */
    private fun getEventAttachmentInfo(post: PostViewData): List<Pair<String, String>> {
        return when (post.viewType) {
            ITEM_POST_SINGLE_IMAGE -> {
                listOf(
                    Pair("image_attached", "1"),
                    Pair("video_attached", "no"),
                    Pair("document_attached", "no"),
                    Pair("link_attached", "no")
                )
            }

            ITEM_POST_SINGLE_VIDEO -> {
                listOf(
                    Pair("video_attached", "1"),
                    Pair("image_attached", "no"),
                    Pair("document_attached", "no"),
                    Pair("link_attached", "no")
                )
            }

            ITEM_POST_DOCUMENTS -> {
                listOf(
                    Pair("video_attached", "no"),
                    Pair("image_attached", "no"),
                    Pair("document_attached", post.attachments.size.toString()),
                    Pair("link_attached", "no")
                )
            }

            ITEM_POST_MULTIPLE_MEDIA -> {
                val imageCount = post.attachments.count {
                    it.attachmentType == IMAGE
                }
                val imageCountString = if (imageCount == 0) {
                    "no"
                } else {
                    imageCount.toString()
                }
                val videCount = post.attachments.count {
                    it.attachmentType == VIDEO
                }
                val videoCountString = if (videCount == 0) {
                    "no"
                } else {
                    videCount.toString()
                }
                listOf(
                    Pair(
                        "image_attached",
                        imageCountString
                    ),
                    Pair(
                        "video_attached",
                        videoCountString
                    ),
                    Pair("document_attached", "no"),
                    Pair("link_attached", "no")
                )
            }

            else -> {
                return emptyList()
            }
        }
    }

    /**
     * Triggers when the user taps on the bell icon and lands on the notification page
     **/
    fun sendNotificationPageOpenedEvent() {
        LMFeedAnalytics.track(LMFeedAnalytics.Events.NOTIFICATION_PAGE_OPENED)
    }
}