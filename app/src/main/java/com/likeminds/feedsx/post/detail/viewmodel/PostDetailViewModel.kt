package com.likeminds.feedsx.post.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.comment.model.AddCommentRequest
import com.likeminds.likemindsfeed.comment.model.DeleteCommentRequest
import com.likeminds.likemindsfeed.comment.model.LikeCommentRequest
import com.likeminds.likemindsfeed.comment.model.ReplyCommentRequest
import com.likeminds.likemindsfeed.helper.model.GetTaggingListRequest
import com.likeminds.likemindsfeed.helper.model.GetTaggingListResponse
import com.likeminds.likemindsfeed.post.model.GetPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _deleteCommentResponse = MutableLiveData<String>()
    val deleteCommentResponse: LiveData<String> = _deleteCommentResponse

    private val _postResponse = MutableLiveData<Pair<Int, PostViewData>>()
    val postResponse: LiveData<Pair<Int, PostViewData>> = _postResponse

    /**
     * [taggingData] contains first -> page called
     * second -> Community Members and Groups
     * */
    private val _taggingData = MutableLiveData<Pair<Int, ArrayList<UserTagViewData>>?>()
    val taggingData: LiveData<Pair<Int, ArrayList<UserTagViewData>>?> = _taggingData

    sealed class ErrorMessageEvent {
        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()
        data class GetTaggingList(val errorMessage: String?) : ErrorMessageEvent()
        data class LikeComment(
            val commentId: String,
            val errorMessage: String?
        ) : ErrorMessageEvent()

        data class AddComment(val errorMessage: String?) : ErrorMessageEvent()
        data class DeleteComment(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    companion object {
        const val PAGE_SIZE = 10
    }

    fun getPost(postId: String, page: Int) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetPostRequest.Builder()
                .postId(postId)
                .page(page)
                .pageSize(PAGE_SIZE)
                .build()

            val response = lmFeedClient.getPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                _postResponse.postValue(
                    Pair(
                        page,
                        ViewDataConverter.convertPost(post, users)
                    )
                )
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetPost(response.errorMessage))
            }
        }
    }

    //for like/unlike a comment
    fun likeComment(postId: String, commentId: String) {
        viewModelScope.launchIO {
            val request = LikeCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .build()

            //call like post api
            val response = lmFeedClient.likeComment(request)

            //check for error
            if (!response.success) {
                errorMessageChannel.send(
                    ErrorMessageEvent.LikeComment(
                        commentId,
                        response.errorMessage
                    )
                )
            }
        }
    }

    fun addComment(postId: String, text: String) {
        viewModelScope.launchIO {
            // builds api request
            val request = AddCommentRequest.Builder()
                .postId(postId)
                .text(text)
                .build()

            val response = lmFeedClient.addComment(request)
            if (response.success) {

            } else {
                errorMessageChannel.send(ErrorMessageEvent.AddComment(response.errorMessage))
            }
        }
    }

    fun replyComment(
        postId: String,
        commentId: String,
        text: String
    ) {
        viewModelScope.launchIO {
            // builds api request
            val request = ReplyCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .text(text)
                .build()

            val response = lmFeedClient.addReplyOnComment(request)
            if (response.success) {

            } else {
                errorMessageChannel.send(ErrorMessageEvent.AddComment(response.errorMessage))
            }
        }
    }

    fun deleteComment(
        postId: String,
        commentId: String?,
        reason: String? = null
    ) {
        viewModelScope.launchIO {
            if (commentId == null) {
                return@launchIO
            }
            val request = DeleteCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .reason(reason)
                .build()

            //call delete post api
            val response = lmFeedClient.deleteComment(request)

            if (response.success) {
                // todo
                _deleteCommentResponse.postValue(postId)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.DeleteComment(response.errorMessage))
            }
        }
    }

    // calls api to get members for tagging
    fun getMembersForTagging(
        page: Int,
        searchName: String
    ) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetTaggingListRequest.Builder()
                .page(page)
                .pageSize(MemberTaggingUtil.PAGE_SIZE)
                .searchName(searchName)
                .build()

            val response = lmFeedClient.getTaggingList(request)
            taggingResponseFetched(page, response)
        }
    }

    // processes tagging list response and sends response to the view
    private fun taggingResponseFetched(
        page: Int,
        response: LMResponse<GetTaggingListResponse>
    ) {
        viewModelScope.launchIO {
            if (response.success) {
                val data = response.data ?: return@launchIO
                _taggingData.postValue(
                    Pair(
                        page,
                        MemberTaggingUtil.getTaggingData(data.members)
                    )
                )
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetTaggingList(response.errorMessage))
            }
        }
    }
}