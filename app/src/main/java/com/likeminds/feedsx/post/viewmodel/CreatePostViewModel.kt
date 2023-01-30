package com.likeminds.feedsx.post.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.likeminds.feedsx.media.MediaRepository
import com.likeminds.feedsx.media.model.MediaViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    fun fetchUriDetails(
        context: Context,
        uris: List<Uri>,
        callback: (media: List<MediaViewData>) -> Unit,
    ) {
        mediaRepository.getLocalUrisDetails(context, uris, callback)
    }
}