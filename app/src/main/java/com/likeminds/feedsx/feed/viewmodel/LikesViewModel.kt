package com.likeminds.feedsx.feed.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.feed.model.COMMENT
import com.likeminds.feedsx.feed.model.LikeViewData
import com.likeminds.feedsx.feed.model.POST
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.comment.model.GetCommentLikesRequest
import com.likeminds.likemindsfeed.comment.model.GetCommentLikesResponse
import com.likeminds.likemindsfeed.post.model.GetPostLikesRequest
import com.likeminds.likemindsfeed.post.model.GetPostLikesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LikesViewModel @Inject constructor() : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _getLikesDataResponse: MutableLiveData<Pair<List<LikeViewData>, Int>> =
        MutableLiveData()
    val getLikesDataResponse: LiveData<Pair<List<LikeViewData>, Int>> = _getLikesDataResponse

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        const val PAGE_SIZE = 20
    }

    fun getLikesData(
        postId: String,
        commentId: String?,
        entityType: Int,
        page: Int
    ) {
        viewModelScope.launchIO {
            if (entityType == POST) {
                val request = GetPostLikesRequest.Builder()
                    .postId(postId)
                    .page(page)
                    .pageSize(PAGE_SIZE)
                    .build()

                postLikesDataFetched(lmFeedClient.getPostLikes(request))
            } else if (entityType == COMMENT) {
                val request = GetCommentLikesRequest.Builder()
                    .postId(postId)
                    .commentId(commentId!!)
                    .page(page)
                    .pageSize(PAGE_SIZE)
                    .build()

                commentLikesDataFetched(lmFeedClient.getCommentLikes(request))
            }
        }
    }

    private fun postLikesDataFetched(response: LMResponse<GetPostLikesResponse>) {
        if (response.success) {
            val data = response.data ?: return
            val totalLikes = data.totalCount
            val likes = data.likes

            val listOfLikeViewData = likes.map {
                ViewDataConverter.convertLikes(it, data.users)
            }
            _getLikesDataResponse.postValue(Pair(listOfLikeViewData, totalLikes))
        } else {
            _getLikesDataResponse.postValue(Pair(emptyList(), 0))
            _errorMessage.postValue(response.errorMessage)
        }
    }

    private fun commentLikesDataFetched(response: LMResponse<GetCommentLikesResponse>) {
        if (response.success) {
            val data = response.data ?: return
            val totalLikes = data.totalCount
            val likes = data.likes

            val listOfLikeViewData = likes.map {
                ViewDataConverter.convertLikes(it, data.users)
            }
            _getLikesDataResponse.postValue(Pair(listOfLikeViewData, totalLikes))
        } else {
            _getLikesDataResponse.postValue(Pair(emptyList(), 0))
            _errorMessage.postValue(response.errorMessage)
        }
    }
}