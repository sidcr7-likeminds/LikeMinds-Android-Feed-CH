package com.likeminds.feedsx.post.edit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.post.model.EditPostRequest
import com.likeminds.likemindsfeed.post.model.GetPostRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class LMFeedEditPostViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    sealed class PostDataEvent {
        data class GetPost(val post: PostViewData) : PostDataEvent()

        data class EditPost(val post: PostViewData) : PostDataEvent()
    }

    private val postDataEventChannel = Channel<PostDataEvent>(Channel.BUFFERED)
    val postDataEventFlow = postDataEventChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()
        data class EditPost(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    // to get the Post to be edited
    fun getPost(postId: String) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetPostRequest.Builder()
                .postId(postId)
                .page(1)
                .pageSize(5)
                .build()

            // calls api
            val response = lmFeedClient.getPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                val topics = data.topics
                postDataEventChannel.send(
                    PostDataEvent.GetPost(
                        ViewDataConverter.convertPost(
                            post,
                            users,
                            topics
                        )
                    )
                )
            } else {
                errorEventChannel.send(ErrorMessageEvent.GetPost(response.errorMessage))
            }
        }
    }

    // calls EditPost API and posts the response in LiveData
    fun editPost(
        postId: String,
        postTextContent: String?,
        attachments: List<AttachmentViewData>? = null,
        ogTags: LinkOGTagsViewData? = null,
        selectedTopics: List<LMFeedTopicViewData>? = null
    ) {
        viewModelScope.launchIO {
            var updatedText = postTextContent?.trim()
            if (updatedText.isNullOrEmpty()) {
                updatedText = null
            }

            val topicIds = selectedTopics?.map {
                it.id
            }

            val request =
                if (attachments != null) {
                    // if the post has any file attachments
                    EditPostRequest.Builder()
                        .postId(postId)
                        .text(updatedText)
                        .attachments(ViewDataConverter.createAttachments(attachments))
                        .topicIds(topicIds)
                        .build()
                } else {
                    // if the post does not have any file attachments
                    val requestBuilder = EditPostRequest.Builder()
                        .postId(postId)
                        .text(updatedText)
                        .topicIds(topicIds)
                    if (ogTags != null) {
                        // if the post has ogTags
                        requestBuilder.attachments(ViewDataConverter.convertAttachments(ogTags))
                    }
                    requestBuilder.build()
                }

            // calls api
            val response = lmFeedClient.editPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                val topics = data.topics
                val postViewData = ViewDataConverter.convertPost(post, users, topics)
                postDataEventChannel.send(PostDataEvent.EditPost(postViewData))

                // sends post edited event
                sendPostEditedEvent(postViewData)
            } else {
                errorEventChannel.send(ErrorMessageEvent.EditPost(response.errorMessage))
            }
        }
    }

    /**
     * Triggers when the user edits a post
     **/
    private fun sendPostEditedEvent(
        post: PostViewData
    ) {
        val postType = ViewUtils.getPostTypeFromViewType(post.viewType)
        val postCreatorUUID = post.user.sdkClientInfoViewData.uuid
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.POST_EDITED,
            mapOf(
                "created_by_uuid" to postCreatorUUID,
                LMFeedAnalytics.Keys.POST_ID to post.id,
                "post_type" to postType,
            )
        )
    }
}