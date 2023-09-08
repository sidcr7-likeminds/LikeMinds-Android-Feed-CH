package com.likeminds.feedsx.post.create.view.adapter.databinder.createpostmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemCreatePostSingleImageBinding
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.post.create.util.CreatePostListener
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE

class LMFeedItemCreatePostMultipleMediaImageViewDataBinder constructor(
    private val listener: CreatePostListener
) : ViewDataBinder<LmFeedItemCreatePostSingleImageBinding, AttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE

    override fun createBinder(parent: ViewGroup): LmFeedItemCreatePostSingleImageBinding {
        return LmFeedItemCreatePostSingleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemCreatePostSingleImageBinding,
        data: AttachmentViewData,
        position: Int
    ) {
        // gets the shimmer drawable for placeholder
        val shimmerDrawable = ViewUtils.getShimmer()

        ImageBindingUtil.loadImage(
            binding.ivSingleImagePost,
            data.attachmentMeta.uri,
            placeholder = shimmerDrawable
        )

        binding.ivCrossImage.setOnClickListener {
            listener.onMediaRemoved(position, IMAGE)
        }
    }
}