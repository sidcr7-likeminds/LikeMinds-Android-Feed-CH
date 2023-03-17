package com.likeminds.feedsx.report.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.report.model.ReportTagViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.moderation.model.GetReportTagsRequest
import com.likeminds.likemindsfeed.moderation.model.GetReportTagsResponse
import com.likeminds.likemindsfeed.moderation.model.PostReportRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _listOfTagViewData = MutableLiveData<List<ReportTagViewData>>()
    val listOfTagViewData = _listOfTagViewData

    private val _postReportResponse = MutableLiveData<Boolean>()
    val postReportResponse = _postReportResponse

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getReportTags(type: Int) {
        viewModelScope.launchIO {
            val request = GetReportTagsRequest.Builder()
                .type(type)
                .build()

            reportTagsFetched(lmFeedClient.getReportTags(request))
        }
    }

    private fun reportTagsFetched(response: LMResponse<GetReportTagsResponse>) {
        if (response.success) {
            val data = response.data ?: return
            val tags = data.tags
            val tagsViewData = tags.map {
                ViewDataConverter.convertReportTag(it)
            }
            _listOfTagViewData.postValue(tagsViewData)
        } else {
            _errorMessage.postValue(response.errorMessage)
        }
    }

    fun postReport(
        entityId: String,
        entityCreatorId: String,
        entityType: Int,
        tagId: Int?,
        reason: String?,
        link: String? = null,
    ) {
        viewModelScope.launchIO {
            //if reason is empty then send [null] in request
            val updatedReason = if (reason.isNullOrEmpty()) null else reason

            val request = PostReportRequest.Builder()
                .entityId(entityId)
                .entityCreatorId(entityCreatorId)
                .entityType(entityType)
                .tagId(tagId ?: 0)
                .reason(updatedReason)
                .link(link)
                .build()

            val response = lmFeedClient.postReport(request)
            if (response.success) {
                _postReportResponse.postValue(true)
            } else {
                _errorMessage.postValue(response.errorMessage)
            }
        }
    }
}