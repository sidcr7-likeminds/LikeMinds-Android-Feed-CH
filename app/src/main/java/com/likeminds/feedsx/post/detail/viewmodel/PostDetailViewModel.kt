package com.likeminds.feedsx.post.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.LMAnalytics
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.comment.model.*
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

    private val _addCommentResponse = MutableLiveData<CommentViewData>()
    val addCommentResponse: LiveData<CommentViewData> = _addCommentResponse

    // it holds pair of [parentCommentId] and [replyComment]
    private val _addReplyResponse = MutableLiveData<Pair<String, CommentViewData>>()
    val addReplyResponse: LiveData<Pair<String, CommentViewData>> = _addReplyResponse

    /**
     * it holds the Pair of [commentId] and [parentCommentId]
     * if comment level is 0 then [parentCommentId] is null
     * if comment level is 1 then [parentCommentId] is non null
     */
    private val _deleteCommentResponse = MutableLiveData<Pair<String, String?>>()
    val deleteCommentResponse: LiveData<Pair<String, String?>> = _deleteCommentResponse

    // it holds the Pair of [page] and [postViewData]
    private val _postResponse = MutableLiveData<Pair<Int, PostViewData>>()
    val postResponse: LiveData<Pair<Int, PostViewData>> = _postResponse

    // it holds the Pair of [page] and [commentViewData]
    private val _getCommentResponse = MutableLiveData<Pair<Int, CommentViewData>>()
    val getCommentResponse: LiveData<Pair<Int, CommentViewData>> = _getCommentResponse

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
        data class GetComment(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    companion object {
        const val PAGE_SIZE = 10
        const val REPLIES_PAGE_SIZE = 5
    }

    // to getPost and paginated comments
    fun getPost(postId: String, page: Int) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetPostRequest.Builder()
                .postId(postId)
                .page(page)
                .pageSize(PAGE_SIZE)
                .build()

            // calls api
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

    // for adding comment on post
    fun addComment(postId: String, text: String) {
        viewModelScope.launchIO {
            // builds api request
            val request = AddCommentRequest.Builder()
                .postId(postId)
                .text(text)
                .build()

            // calls api
            val response = lmFeedClient.addComment(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val comment = data.comment
                val users = data.users
                sendCommentPostedEvent(postId, comment.id)

                _addCommentResponse.postValue(
                    ViewDataConverter.convertComment(
                        comment,
                        users,
                        postId
                    )
                )
            } else {
                errorMessageChannel.send(ErrorMessageEvent.AddComment(response.errorMessage))
            }
        }
    }

    private fun sendCommentPostedEvent(postId: String, commentId: String) {
        LMAnalytics.track(
            LMAnalytics.COMMENT_POSTED,
            mapOf(
                LMAnalytics.POST_ID to postId,
                LMAnalytics.COMMENT_ID to commentId
            )
        )
    }

    // for replying on a comment on the post
    fun replyComment(
        postId: String,
        parentCommentId: String,
        text: String
    ) {
        viewModelScope.launchIO {
            // builds api request
            val request = ReplyCommentRequest.Builder()
                .postId(postId)
                .commentId(parentCommentId)
                .text(text)
                .build()

            // calls api
            val response = lmFeedClient.replyComment(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val comment = data.comment
                val users = data.users
                // todo: add user id
                sendReplyPostedEvent(
                    "user_id",
                    postId,
                    parentCommentId,
                    comment.id
                )

                _addReplyResponse.postValue(
                    Pair(
                        parentCommentId,
                        ViewDataConverter.convertComment(
                            comment,
                            users,
                            postId,
                            parentCommentId
                        )
                    )
                )
            } else {
                errorMessageChannel.send(ErrorMessageEvent.AddComment(response.errorMessage))
            }
        }
    }

    private fun sendReplyPostedEvent(
        userId: String,
        postId: String,
        parentCommentId: String,
        commentId: String,
    ) {
        LMAnalytics.track(
            LMAnalytics.COMMENT_POSTED,
            mapOf(
                LMAnalytics.USER_ID to userId,
                LMAnalytics.POST_ID to postId,
                LMAnalytics.COMMENT_ID to parentCommentId,
                LMAnalytics.COMMENT_REPLY_ID to commentId
            )
        )
    }

    // to get comment with paginated replies
    fun getComment(
        postId: String,
        commentId: String,
        page: Int
    ) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .page(page)
                .pageSize(REPLIES_PAGE_SIZE)
                .build()

            // calls api
            val response = lmFeedClient.getComment(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val comment = data.comment
                val users = data.users
                _getCommentResponse.postValue(
                    Pair(
                        page,
                        ViewDataConverter.convertComment(
                            comment,
                            users,
                            postId
                        )
                    )
                )
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetComment(response.errorMessage))
            }
        }
    }

    // for deleting comment/reply
    fun deleteComment(
        postId: String,
        commentId: String,
        parentCommentId: String? = null,
        reason: String? = null
    ) {
        viewModelScope.launchIO {
            val request = DeleteCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .reason(reason)
                .build()

            //call delete comment api
            val response = lmFeedClient.deleteComment(request)

            if (response.success) {
                sendCommentReplyDeletedEvent(
                    postId,
                    commentId,
                    parentCommentId
                )
                _deleteCommentResponse.postValue(Pair(commentId, parentCommentId))
            } else {
                errorMessageChannel.send(ErrorMessageEvent.DeleteComment(response.errorMessage))
            }
        }
    }

    private fun sendCommentReplyDeletedEvent(
        postId: String,
        commentId: String,
        parentCommentId: String?
    ) {
        if (parentCommentId == null) {
            // comment deleted event
            LMAnalytics.track(
                LMAnalytics.COMMENT_DELETED,
                mapOf(
                    LMAnalytics.POST_ID to postId,
                    LMAnalytics.COMMENT_ID to commentId
                )
            )
        } else {
            // reply deleted event
            LMAnalytics.track(
                LMAnalytics.REPLY_DELETED,
                mapOf(
                    LMAnalytics.POST_ID to postId,
                    LMAnalytics.COMMENT_ID to parentCommentId,
                    LMAnalytics.COMMENT_REPLY_ID to commentId,
                )
            )
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