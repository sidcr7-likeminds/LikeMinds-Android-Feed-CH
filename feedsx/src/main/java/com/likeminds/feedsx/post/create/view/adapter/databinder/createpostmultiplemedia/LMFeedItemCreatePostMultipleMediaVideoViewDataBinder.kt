package com.likeminds.feedsx.post.create.view.adapter.databinder.createpostmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemCreatePostSingleVideoBinding
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.post.create.util.CreatePostListener
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO

class LMFeedItemCreatePostMultipleMediaVideoViewDataBinder constructor(
    private val listener: CreatePostListener
) : ViewDataBinder<LmFeedItemCreatePostSingleVideoBinding, AttachmentViewData>() {
    override val viewType: Int
        get() = ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO

    override fun createBinder(parent: ViewGroup): LmFeedItemCreatePostSingleVideoBinding {
        val inflater = LayoutInflater.from(parent.context)
        return LmFeedItemCreatePostSingleVideoBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: LmFeedItemCreatePostSingleVideoBinding,
        data: AttachmentViewData,
        position: Int
    ) {
        binding.ivCrossVideo.setOnClickListener {
            listener.onMediaRemoved(position, VIDEO)
        }
    }
}