package com.likeminds.feedsx.report.viewmodel

import androidx.lifecycle.*
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.report.model.ReportTagViewData
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.moderation.model.*
import javax.inject.Inject

class ReportViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _listOfTagViewData = MutableLiveData<List<ReportTagViewData>>()
    val listOfTagViewData: LiveData<List<ReportTagViewData>> = _listOfTagViewData

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _postReportResponse = MutableLiveData<Boolean>()
    val postReportResponse: LiveData<Boolean> = _postReportResponse

    companion object {
        const val REPORT_TAG_TYPE = 3
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
        uuid: String,
        entityType: Int,
        tagId: Int?,
        reason: String?
    ) {
        viewModelScope.launchIO {
            //if reason is empty then send [null] in request
            val updatedReason = if (reason.isNullOrEmpty()) null else reason

            val request = PostReportRequest.Builder()
                .entityId(entityId)
                .uuid(uuid)
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

    /**
     * Triggers when the user reports a post
     **/
    fun sendPostReportedEvent(
        postId: String,
        uuid: String,
        postType: String,
        reason: String
    ) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.POST_REPORTED,
            mapOf(
                "created_by_uuid" to uuid,
                LMFeedAnalytics.Keys.POST_ID to postId,
                "report_reason" to reason,
                "post_type" to postType,
            )
        )
    }

    /**
     * Triggers when the user reports a comment
     **/
    fun sendCommentReportedEvent(
        postId: String,
        uuid: String,
        commentId: String,
        reason: String
    ) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.COMMENT_REPORTED,
            mapOf(
                LMFeedAnalytics.Keys.POST_ID to postId,
                LMFeedAnalytics.Keys.UUID to uuid,
                LMFeedAnalytics.Keys.COMMENT_ID to commentId,
                "reason" to reason,
            )
        )
    }

    /**
     * Triggers when the user reports a reply
     **/
    fun sendReplyReportedEvent(
        postId: String,
        uuid: String,
        parentCommentId: String?,
        replyId: String,
        reason: String
    ) {
        val updatedParentId = parentCommentId ?: ""
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.REPLY_REPORTED,
            mapOf(
                LMFeedAnalytics.Keys.POST_ID to postId,
                LMFeedAnalytics.Keys.COMMENT_ID to updatedParentId,
                LMFeedAnalytics.Keys.COMMENT_REPLY_ID to replyId,
                LMFeedAnalytics.Keys.UUID to uuid,
                "reason" to reason,
            )
        )
    }
}