package com.likeminds.feedsx.notificationfeed.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.notificationfeed.model.ActivityViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.notificationfeed.model.GetNotificationFeedRequest
import com.likeminds.likemindsfeed.notificationfeed.model.MarkReadNotificationRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class NotificationFeedViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _notificationFeedResponse = MutableLiveData<Pair<Int, List<ActivityViewData>>>()
    val notificationFeedResponse: LiveData<Pair<Int, List<ActivityViewData>>> =
        _notificationFeedResponse

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class GetNotificationFeed(val errorMessage: String?) : ErrorMessageEvent()
        data class MarkReadNotification(val errorMessage: String?) : ErrorMessageEvent()
    }

    companion object {
        const val PAGE_SIZE = 20
    }

    // get notification feed
    fun getNotificationFeed(page: Int) {
        viewModelScope.launchIO {
            val request = GetNotificationFeedRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)
                .build()

            //call notification feed api
            val response = lmFeedClient.getNotificationFeed(request)

            if (response.success) {
                val data = response.data ?: return@launchIO
                val activities = data.activities
                val usersMap = data.users

                //convert to view data
                val listOfActivityViewData =
                    ViewDataConverter.convertActivities(activities, usersMap)

                //send it to ui
                _notificationFeedResponse.postValue(Pair(page, listOfActivityViewData))
            } else {
                //for error
                errorMessageChannel.send(ErrorMessageEvent.GetNotificationFeed(response.errorMessage))
            }
        }
    }

    fun markReadNotification(activityId: String) {
        viewModelScope.launchIO {
            val request = MarkReadNotificationRequest.Builder()
                .activityId(activityId)
                .build()

            //call notification feed api
            val response = lmFeedClient.markReadNotification(request)

            if (!response.success) {
                errorMessageChannel.send(ErrorMessageEvent.MarkReadNotification(response.errorMessage))
            }
        }
    }
}