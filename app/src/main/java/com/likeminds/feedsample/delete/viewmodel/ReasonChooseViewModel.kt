package com.likeminds.feedsample.delete.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsample.delete.model.ReasonChooseViewData
import com.likeminds.feedsample.utils.ViewDataConverter
import com.likeminds.feedsample.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.moderation.model.GetReportTagsRequest
import com.likeminds.likemindsfeed.moderation.model.GetReportTagsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReasonChooseViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _listOfTagViewData = MutableLiveData<List<ReasonChooseViewData>>()
    val listOfTagViewData: LiveData<List<ReasonChooseViewData>> = _listOfTagViewData

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        const val REPORT_TAG_TYPE = 0
    }

    // calls api and fetches report tags
    fun getReportTags() {
        viewModelScope.launchIO {
            // builds report tag request
            val request = GetReportTagsRequest.Builder()
                .type(REPORT_TAG_TYPE)
                .build()

            reportTagsFetched(lmFeedClient.getReportTags(request))
        }
    }

    // processes report tag request and sends response to UI
    private fun reportTagsFetched(response: LMResponse<GetReportTagsResponse>) {
        if (response.success) {
            val data = response.data ?: return
            val tags = data.tags
            val tagsViewData = ViewDataConverter.convertDeleteTag(tags)
            val lastTag = tagsViewData.last().toBuilder()
                .hideBottomLine(true)
                .build()

            tagsViewData[tagsViewData.lastIndex] = lastTag

            _listOfTagViewData.postValue(tagsViewData.toList())
        } else {
            _errorMessage.postValue(response.errorMessage)
        }
    }
}