package com.likeminds.feedsx

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.likeminds.feedsx.feed.UserRepository
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.UserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.helper.model.RegisterDeviceRequest
import com.likeminds.likemindsfeed.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindsfeed.sdk.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InitiateViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _userResponse = MutableLiveData<UserViewData>()
    val userResponse: LiveData<UserViewData> = _userResponse

    private val _logoutResponse = MutableLiveData<Boolean>()
    val logoutResponse: LiveData<Boolean> = _logoutResponse

    private val _initiateErrorMessage = MutableLiveData<String?>()
    val initiateErrorMessage: LiveData<String?> = _initiateErrorMessage

    /***
     * calls InitiateUser API
     * store user:{} in db
     * and posts the response in LiveData
     * */
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
                    val id = user?.userUniqueId ?: ""

                    //add user in local db
                    addUser(user)

                    //save user.id in local prefs
                    userPreferences.saveUserUniqueId(id)

                    //post the user response in LiveData
                    _userResponse.postValue(ViewDataConverter.convertUser(user))
                }
            } else {
                _initiateErrorMessage.postValue(initiateResponse.errorMessage)
            }
        }
    }

    //add user:{} into local db
    private fun addUser(user: User?) {
        if (user == null) return
        viewModelScope.launchIO {
            //convert user into userEntity
            val userEntity = ViewDataConverter.createUserEntity(user)
            //add it to local db
            userRepository.insertUser(userEntity)

            //call member state api
            getMemberState()

            //call register device api
            registerDevice()
        }
    }

    //call member state api
    private fun getMemberState() {
        viewModelScope.launchIO {
            //get member state response
            val memberStateResponse = lmFeedClient.getMemberState().data

            val memberState = memberStateResponse?.state ?: return@launchIO
            val isOwner = memberStateResponse.isOwner
            val userId = memberStateResponse.userUniqueId

            //get existing userEntity
            var userEntity = userRepository.getUser(userId)

            //updated userEntity
            userEntity = userEntity.toBuilder().state(memberState).isOwner(isOwner).build()

            //update userEntity in local db
            userRepository.updateUser(userEntity)
        }
    }

    //call register device
    private fun registerDevice() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    FeedSXApplication.LOG_TAG,
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@addOnCompleteListener
            }

            val token = task.result.toString()
            pushToken(token)
        }
    }

    private fun pushToken(token: String) {
        viewModelScope.launchIO {
            //create request
            val request = RegisterDeviceRequest.Builder()
                .deviceId(userPreferences.getDeviceId())
                .token(token)
                .build()

            //call api
            lmFeedClient.registerDevice(request)
        }
    }
}