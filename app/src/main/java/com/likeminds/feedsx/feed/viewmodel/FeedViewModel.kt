package com.likeminds.feedsx.feed.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.feed.UserRepository
import com.likeminds.feedsx.utils.UserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.RegisterDeviceRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserResponse
import com.likeminds.likemindsfeed.sdk.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _initiateUserResponse = MutableLiveData<LMResponse<InitiateUserResponse>>()
    val initiateUserResponse: LiveData<LMResponse<InitiateUserResponse>> = _initiateUserResponse

    // calls InitiateUser API
    // store user:{} in db
    // and posts the response in LiveData
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
                val user = initiateResponse.data?.user
                val id = user?.id ?: -1

                //add user in local db
                addUser(user)

                //save user.id in local prefs
                userPreferences.saveMemberId(id)

                getMemberState()
                registerDevice()
            }
            //send response to UI
            _initiateUserResponse.postValue(initiateResponse)
        }
    }

    //add user:{} into local db
    private fun addUser(user: User?) {
        if (user == null) return
        viewModelScope.launchIO {
            val userEntity = ViewDataConverter.convertUser(user)
            userRepository.insertUser(userEntity)
        }
    }

    private fun getMemberState() {
        viewModelScope.launchIO {
            val memberStateResponse = lmFeedClient.getMemberState().data

            val memberState = memberStateResponse?.state ?: return@launchIO
            val isOwner = memberStateResponse.isOwner
            val userId = memberStateResponse.id

            var userEntity = userRepository.getUser(userId)

            userEntity = userEntity.toBuilder().state(memberState).isOwner(isOwner).build()
            userRepository.updateUser(userEntity)
        }
    }

    private fun registerDevice() {
        viewModelScope.launchIO {
            val request = RegisterDeviceRequest.Builder()
                .deviceId(userPreferences.getDeviceId())
                .token("YUYUYUYUYUY") //todo fix it with proper token
                .build()

            lmFeedClient.registerDevice(request)
        }
    }
}