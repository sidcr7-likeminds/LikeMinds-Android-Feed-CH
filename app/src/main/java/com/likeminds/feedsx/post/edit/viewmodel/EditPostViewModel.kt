package com.likeminds.feedsx.post.edit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.model.LinkOGTagsViewData
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.post.model.EditPostRequest
import com.likeminds.likemindsfeed.post.model.GetPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _postResponse = MutableLiveData<PostViewData>()
    val postResponse: LiveData<PostViewData> = _postResponse

    private val _postEdited = MutableLiveData<Boolean>()
    val postEdited: LiveData<Boolean> = _postEdited

    sealed class ErrorMessageEvent {
        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()
        data class EditPost(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    // to getPost
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
                _postResponse.postValue(ViewDataConverter.convertPost(post, users))
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
        ogTags: LinkOGTagsViewData? = null
    ) {
        viewModelScope.launchIO {
            var updatedText = postTextContent?.trim()
            if (updatedText.isNullOrEmpty()) {
                updatedText = null
            }
            val request =
                if (attachments != null) {
                    // if the post has any file attachments
                    EditPostRequest.Builder()
                        .postId(postId)
                        .text(updatedText)
                        .attachments(ViewDataConverter.createAttachments(attachments))
                        .build()
                } else {
                    // if the post does not have any file attachments
                    val requestBuilder = EditPostRequest.Builder()
                        .postId(postId)
                        .text(updatedText)
                    if (ogTags != null) {
                        // if the post has ogTags
                        requestBuilder.attachments(ViewDataConverter.convertAttachments(ogTags))
                    }
                    requestBuilder.build()
                }
            val response = lmFeedClient.editPost(request)
            if (response.success) {
                _postEdited.postValue(true)
            } else {
                errorEventChannel.send(ErrorMessageEvent.EditPost(response.errorMessage))
            }
        }
    }
}