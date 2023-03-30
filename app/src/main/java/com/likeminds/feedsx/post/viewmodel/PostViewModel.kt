package com.likeminds.feedsx.post.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.utils.UserPreferences
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.post.model.DeletePostRequest
import com.likeminds.likemindsfeed.post.model.LikePostRequest
import com.likeminds.likemindsfeed.post.model.PinPostRequest
import com.likeminds.likemindsfeed.post.model.SavePostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _deletePostResponse = MutableLiveData<String>()
    val deletePostResponse: LiveData<String> = _deletePostResponse

    private val _pinPostResponse = MutableLiveData<String>()
    val pinPostResponse: LiveData<String> = _pinPostResponse

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class LikePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()
        data class SavePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()
        data class DeletePost(val errorMessage: String?) : ErrorMessageEvent()
        data class PinPost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()
    }

    // returns user unique id from user prefs
    fun getUserUniqueId(): String {
        return userPreferences.getUserUniqueId()
    }

    //for like/unlike a post
    fun likePost(postId: String) {
        viewModelScope.launchIO {
            val request = LikePostRequest.Builder()
                .postId(postId)
                .build()

            //call like post api
            val response = lmFeedClient.likePost(request)

            //check for error
            if (!response.success) {
                errorMessageChannel.send(
                    ErrorMessageEvent.LikePost(
                        postId,
                        response.errorMessage
                    )
                )
            }
        }
    }

    //for save/un-save a post
    fun savePost(postId: String) {
        viewModelScope.launchIO {
            val request = SavePostRequest.Builder()
                .postId(postId)
                .build()

            //call save post api
            val response = lmFeedClient.savePost(request)

            //check for error
            if (!response.success) {
                errorMessageChannel.send(
                    ErrorMessageEvent.SavePost(
                        postId,
                        response.errorMessage
                    )
                )
            }
        }
    }

    //for delete post
    fun deletePost(
        postId: String,
        reason: String? = null
    ) {
        viewModelScope.launchIO {
            val request = DeletePostRequest.Builder()
                .postId(postId)
                .deleteReason(reason)
                .build()

            //call delete post api
            val response = lmFeedClient.deletePost(request)

            if (response.success) {
                _deletePostResponse.postValue(postId)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.DeletePost(response.errorMessage))
            }
        }
    }

    //for pin/unpin post
    fun pinPost(postId: String) {
        viewModelScope.launchIO {
            val request = PinPostRequest.Builder()
                .postId(postId)
                .build()

            //call pin api
            val response = lmFeedClient.pinPost(request)

            if (response.success) {
                _pinPostResponse.postValue(postId)
            } else {
                errorMessageChannel.send(
                    ErrorMessageEvent.PinPost(
                        postId,
                        response.errorMessage
                    )
                )
            }
        }
    }
}