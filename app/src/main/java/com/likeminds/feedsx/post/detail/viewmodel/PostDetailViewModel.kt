package com.likeminds.feedsx.post.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.delete.model.DELETE_TYPE_COMMENT
import com.likeminds.feedsx.delete.model.DELETE_TYPE_POST
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.comment.model.DeleteCommentRequest
import com.likeminds.likemindsfeed.post.model.DeletePostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _deleteEntityResponse = MutableLiveData<Pair<Int, Boolean>>()
    val deleteEntityResponse = _deleteEntityResponse

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    // calls DeletePost/DeleteComment API and posts the response/error message in LiveData
    fun deleteEntity(
        postId: String,
        entityType: Int,
        commentId: String? = null,
        reason: String? = null
    ) {
        viewModelScope.launchIO {
            //if reason is empty then send [null] in request
            val updatedReason = if (reason.isNullOrEmpty()) null else reason

            if (entityType == DELETE_TYPE_POST) {
                // processes delete post request if entity = post
                val request = DeletePostRequest.Builder()
                    .postId(postId)
                    .deleteReason(updatedReason)
                    .build()

                val response = lmFeedClient.deletePost(request)
                if (response.success) {
                    _deleteEntityResponse.postValue(Pair(DELETE_TYPE_POST, true))
                } else {
                    _errorMessage.postValue(response.errorMessage)
                }
            } else if (entityType == DELETE_TYPE_COMMENT) {
                // processes delete comment request if entity = comment
                val updatedCommentId = commentId ?: ""
                val request = DeleteCommentRequest.Builder()
                    .postId(postId)
                    .commentId(updatedCommentId)
                    .reason(updatedReason)
                    .build()

                val response = lmFeedClient.deleteComment(request)
                if (response.success) {
                    _deleteEntityResponse.postValue(Pair(DELETE_TYPE_COMMENT, true))
                } else {
                    _errorMessage.postValue(response.errorMessage)
                }
            }
        }
    }
}