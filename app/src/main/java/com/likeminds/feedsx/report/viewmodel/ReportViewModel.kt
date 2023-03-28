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
    val listOfTagViewData: LiveData<List<ReportTagViewData>> = _listOfTagViewData

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _postReportResponse = MutableLiveData<Boolean>()
    val postReportResponse: LiveData<Boolean> = _postReportResponse

    companion object {
        const val REPORT_TAG_TYPE = 0
    }

    //Get report tags for reporting
    fun getReportTags() {
        viewModelScope.launchIO {
            val request = GetReportTagsRequest.Builder()
                .type(REPORT_TAG_TYPE)
                .build()

            reportTagsFetched(lmFeedClient.getReportTags(request))
        }
    }

    //to convert to TagViewData
    private fun reportTagsFetched(response: LMResponse<GetReportTagsResponse>) {
        if (response.success) {
            val data = response.data ?: return
            val tags = data.tags
            val tagsViewData = ViewDataConverter.convertReportTag(tags)
            _listOfTagViewData.postValue(tagsViewData)
        } else {
            _errorMessage.postValue(response.errorMessage)
        }
    }

    //for reporting post/comment/reply
    fun postReport(
        entityId: String,
        entityCreatorId: String,
        entityType: Int,
        tagId: Int?,
        reason: String?
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