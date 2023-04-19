package com.likeminds.feedsx.post.edit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.feed.UserWithRightsRepository
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.model.LinkOGTagsViewData
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.UserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.DecodeUrlRequest
import com.likeminds.likemindsfeed.helper.model.DecodeUrlResponse
import com.likeminds.likemindsfeed.helper.model.GetTaggingListRequest
import com.likeminds.likemindsfeed.helper.model.GetTaggingListResponse
import com.likeminds.likemindsfeed.post.model.EditPostRequest
import com.likeminds.likemindsfeed.post.model.GetPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val userWithRightsRepository: UserWithRightsRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _userData = MutableLiveData<UserViewData>()
    val userData: LiveData<UserViewData> = _userData

    private val _postResponse = MutableLiveData<PostViewData>()
    val postResponse: LiveData<PostViewData> = _postResponse

    private val _decodeUrlResponse = MutableLiveData<LinkOGTagsViewData>()
    val decodeUrlResponse: LiveData<LinkOGTagsViewData> = _decodeUrlResponse

    /**
     * [taggingData] contains first -> page called
     * second -> Community Members and Groups
     * */
    private val _taggingData = MutableLiveData<Pair<Int, ArrayList<UserTagViewData>>?>()
    val taggingData: LiveData<Pair<Int, ArrayList<UserTagViewData>>?> = _taggingData

    private val _postEdited = MutableLiveData<Boolean>()
    val postEdited: LiveData<Boolean> = _postEdited

    sealed class ErrorMessageEvent {
        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()
        data class DecodeUrl(val errorMessage: String?) : ErrorMessageEvent()
        data class GetTaggingList(val errorMessage: String?) : ErrorMessageEvent()
        data class EditPost(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    // fetches user from DB and posts in the live data
    fun fetchUserFromDB() {
        viewModelScope.launchIO {
            val userId = userPreferences.getUserUniqueId()

            // fetches user from DB with user.id
            val userWithRights = userWithRightsRepository.getUser(userId)
            _userData.postValue(ViewDataConverter.convertUser(userWithRights))
        }
    }

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

    // calls DecodeUrl API
    fun decodeUrl(url: String) {
        viewModelScope.launchIO {
            val request = DecodeUrlRequest.Builder().url(url).build()

            val response = lmFeedClient.decodeUrl(request)
            postDecodeUrlResponse(response)
        }
    }

    // processes and posts the DecodeUrl response in LiveData
    private fun postDecodeUrlResponse(response: LMResponse<DecodeUrlResponse>) {
        viewModelScope.launchIO {
            if (response.success) {
                // processes link og tags if API call was successful
                val data = response.data ?: return@launchIO
                val ogTags = data.ogTags
                _decodeUrlResponse.postValue(ViewDataConverter.convertLinkOGTags(ogTags))
            } else {
                // posts error message if API call failed
                errorEventChannel.send(ErrorMessageEvent.DecodeUrl(response.errorMessage))
            }
        }
    }

    // calls api to get members for tagging
    fun getMembersForTagging(
        page: Int,
        searchName: String
    ) {
        viewModelScope.launchIO {
            val updatedSearchName = searchName.ifEmpty { null } ?: searchName
            val request = GetTaggingListRequest.Builder()
                .page(page)
                .pageSize(MemberTaggingUtil.PAGE_SIZE)
                .searchName(updatedSearchName)
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
                errorEventChannel.send(ErrorMessageEvent.GetTaggingList(response.errorMessage))
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