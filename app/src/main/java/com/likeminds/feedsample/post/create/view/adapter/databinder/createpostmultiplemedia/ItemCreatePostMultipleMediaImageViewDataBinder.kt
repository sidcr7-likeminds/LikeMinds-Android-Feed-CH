package com.likeminds.feedsample.post.create.view.adapter.databinder.createpostmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsample.databinding.ItemCreatePostSingleImageBinding
import com.likeminds.feedsample.media.model.IMAGE
import com.likeminds.feedsample.post.create.util.CreatePostListener
import com.likeminds.feedsample.posttypes.model.AttachmentViewData
import com.likeminds.feedsample.utils.ViewUtils
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.databinding.ImageBindingUtil
import com.likeminds.feedsample.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE

class ItemCreatePostMultipleMediaImageViewDataBinder constructor(
    private val listener: CreatePostListener
) : ViewDataBinder<ItemCreatePostSingleImageBinding, AttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE

    override fun createBinder(parent: ViewGroup): ItemCreatePostSingleImageBinding {
        return ItemCreatePostSingleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemCreatePostSingleImageBinding,
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

        binding.ivCross.setOnClickListener {
            listener.onMediaRemoved(position, IMAGE)
        }
    }
}