package com.likeminds.feedsx.post.edit.viewmodel

import androidx.lifecycle.*
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.feed.UserWithRightsRepository
import com.likeminds.feedsx.posttypes.model.LinkOGTagsViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.LMFeedUserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class HelperViewModel @Inject constructor(
    private val userWithRightsRepository: UserWithRightsRepository,
    private val userPreferences: LMFeedUserPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _decodeUrlResponse = MutableLiveData<LinkOGTagsViewData>()
    val decodeUrlResponse: LiveData<LinkOGTagsViewData> = _decodeUrlResponse

    private val _userData = MutableLiveData<UserViewData>()
    val userData: LiveData<UserViewData> = _userData

    /**
     * [taggingData] contains first -> page called
     * second -> Community Members and Groups
     * */
    private val _taggingData = MutableLiveData<Pair<Int, ArrayList<UserTagViewData>>?>()
    val taggingData: LiveData<Pair<Int, ArrayList<UserTagViewData>>?> = _taggingData

    sealed class ErrorMessageEvent {
        data class DecodeUrl(val errorMessage: String?) : ErrorMessageEvent()
        data class GetTaggingList(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    // updates the user data
    fun updateUserData(userViewData: UserViewData) {
        _userData.postValue(userViewData)
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

    // fetches user from DB and posts in the live data
    fun fetchUserFromDB() {
        viewModelScope.launchIO {
            val userId = userPreferences.getUserUniqueId()

            // fetches user from DB with user.id
            val userWithRights = userWithRightsRepository.getUser(userId)
            _userData.postValue(ViewDataConverter.convertUser(userWithRights))
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

    /**
     * Triggers event when the user tags someone
     * @param uuid user-unique-id
     * @param userCount count of tagged users
     */
    fun sendUserTagEvent(uuid: String, userCount: Int) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.USER_TAGGED_IN_POST,
            mapOf(
                "tagged_user_uuid" to uuid,
                "tagged_user_count" to userCount.toString()
            )
        )
    }

    /**
     * Triggers when the user attaches link
     * @param link - url of the link
     **/
    fun sendLinkAttachedEvent(link: String) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.LINK_ATTACHED_IN_POST,
            mapOf(
                "link" to link
            )
        )
    }
}