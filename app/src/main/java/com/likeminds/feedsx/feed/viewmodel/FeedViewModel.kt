package com.likeminds.feedsx.feed.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.feed.UserRepository
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.UserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.helper.model.RegisterDeviceRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindsfeed.sdk.model.User
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserResponse
import com.likeminds.likemindsfeed.post.model.DeletePostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _userResponse = MutableLiveData<UserViewData>()
    val userResponse: LiveData<UserViewData> = _userResponse

    private val _logoutResponse = MutableLiveData<Boolean>()
    val logoutResponse: LiveData<Boolean> = _logoutResponse

    sealed class ErrorMessageEvent {
        data class InitiateUser(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    /***
     * calls InitiateUser API
     * stores user:{} in db
     * and posts the response in LiveData
     * */
    private val _deletePostResponse = MutableLiveData<Boolean>()
    val deletePostResponse = _deletePostResponse

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    // calls InitiateUser API and posts the response in LiveData
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
                val data = initiateResponse.data ?: return@launchIO
                if (data.logoutResponse != null) {
                    //user is invalid
                    _logoutResponse.postValue(true)
                } else {
                    val user = data.user
                    val id = user?.id ?: -1

                    //add user in local db
                    addUser(user)

                    //save user.id in local prefs
                    userPreferences.saveMemberId(id)

                    //post the user response in LiveData
                    _userResponse.postValue(ViewDataConverter.convertUser(user))
                }
            } else {
                errorEventChannel.send(ErrorMessageEvent.InitiateUser(initiateResponse.errorMessage))
            }
        }
    }

    //add user:{} into local db
    private fun addUser(user: User?) {
        if (user == null) return
        viewModelScope.launchIO {
            //convert user into userEntity
            val userEntity = ViewDataConverter.convertUserEntity(user)
            //add it to local db
            userRepository.insertUser(userEntity)

            //call member state api
            getMemberState()

            //call register device api
            registerDevice()
        }
    }

    private fun getMemberState() {
        viewModelScope.launchIO {
            //get member state response
            val memberStateResponse = lmFeedClient.getMemberState().data

            val memberState = memberStateResponse?.state ?: return@launchIO
            val isOwner = memberStateResponse.isOwner
            val userId = memberStateResponse.id

            //get existing userEntity
            var userEntity = userRepository.getUser(userId)

            //updated userEntity
            userEntity = userEntity.toBuilder().state(memberState).isOwner(isOwner).build()

            //update userEntity in local db
            userRepository.updateUser(userEntity)
        }
    }

    private fun registerDevice() {
        viewModelScope.launchIO {
            //create request
            val request = RegisterDeviceRequest.Builder()
                .deviceId(userPreferences.getDeviceId())
                .token("YUYUYUYUYUY") //todo fix it with proper token
                .build()

            //call api
            lmFeedClient.registerDevice(request)
        }
    }

    // calls DeletePost API and posts the response/error message in LiveData
    fun deletePost(
        postId: String,
        reason: String?
    ) {
        viewModelScope.launchIO {
            val request = DeletePostRequest.Builder()
                .postId(postId)
                .deleteReason(reason)
                .build()

            val response = lmFeedClient.deletePost(request)
            if (response.success) {
                _deletePostResponse.postValue(true)
            } else {
                _errorMessage.postValue(response.errorMessage)
            }
        }
    }
}