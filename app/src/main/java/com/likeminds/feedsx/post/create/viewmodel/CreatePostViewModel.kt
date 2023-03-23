package com.likeminds.feedsx.post.create.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.media.MediaRepository
import com.likeminds.feedsx.media.model.MediaViewData
import com.likeminds.feedsx.posttypes.model.LinkOGTags
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.DecodeUrlRequest
import com.likeminds.likemindsfeed.helper.model.DecodeUrlResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _decodeUrlResponse = MutableLiveData<LinkOGTags>()
    val decodeUrlResponse: LiveData<LinkOGTags> = _decodeUrlResponse

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchUriDetails(
        context: Context,
        uris: List<Uri>,
        callback: (media: List<MediaViewData>) -> Unit,
    ) {
        mediaRepository.getLocalUrisDetails(context, uris, callback)
    }

    fun decodeUrl(url: String) {
        viewModelScope.launchIO {
            val request = DecodeUrlRequest.Builder().url(url).build()

            val response = lmFeedClient.decodeUrl(request)
            postDecodeUrlResponse(response)
        }
    }

    private fun postDecodeUrlResponse(response: LMResponse<DecodeUrlResponse>) {
        if (response.success) {
            // processes link og tags if API call was successful
            val data = response.data ?: return
            val ogTags = data.ogTags
            _decodeUrlResponse.postValue(ViewDataConverter.convertOGTags(ogTags))
        } else {
            // posts error message if API call failed
            _errorMessage.postValue(response.errorMessage)
        }
    }
}