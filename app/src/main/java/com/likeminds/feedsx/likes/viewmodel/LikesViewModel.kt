package com.likeminds.feedsx.likes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.likes.model.COMMENT
import com.likeminds.feedsx.likes.model.LikeViewData
import com.likeminds.feedsx.likes.model.POST
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

    private val _likesResponse: MutableLiveData<Pair<List<LikeViewData>, Int>> =
        MutableLiveData()
    val likesResponse: LiveData<Pair<List<LikeViewData>, Int>> = _likesResponse

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        const val PAGE_SIZE = 20
    }

    // calls API for post likes and comments likes data
    fun getLikesData(
        postId: String,
        commentId: String?,
        entityType: Int,
        page: Int
    ) {
        viewModelScope.launchIO {
            when (entityType) {
                POST -> {
                    // calls getPostLikes API
                    val request = GetPostLikesRequest.Builder()
                        .postId(postId)
                        .page(page)
                        .pageSize(PAGE_SIZE)
                        .build()

                    postLikesDataFetched(lmFeedClient.getPostLikes(request))
                }
                COMMENT -> {
                    // calls getCommentLikes API
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
    }

    // processes post likes api response and posts the data to LiveData
    private fun postLikesDataFetched(response: LMResponse<GetPostLikesResponse>) {
        if (response.success) {
            // processes Likes data if API call was successful
            val data = response.data ?: return
            val totalLikes = data.totalCount
            val likes = data.likes

            val listOfLikeViewData = ViewDataConverter.convertLikes(likes, data.users)
            _likesResponse.postValue(Pair(listOfLikeViewData, totalLikes))
        } else {
            // posts error message if API call failed
            _errorMessage.postValue(response.errorMessage)
        }
    }

    // processes comment like api response and posts the data to LiveData
    private fun commentLikesDataFetched(response: LMResponse<GetCommentLikesResponse>) {
        if (response.success) {
            // processes Likes data if API call was successful
            val data = response.data ?: return
            val totalLikes = data.totalCount
            val likes = data.likes

            val listOfLikeViewData = ViewDataConverter.convertLikes(likes, data.users)
            _likesResponse.postValue(Pair(listOfLikeViewData, totalLikes))
        } else {
            // posts error message if API call failed
            _errorMessage.postValue(response.errorMessage)
        }
    }
}