package com.likeminds.feedsx.post.view.adapter.databinder.createpostmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemCreatePostSingleImageBinding
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE

class ItemCreatePostMultipleMediaImageViewDataBinder :
    ViewDataBinder<ItemCreatePostSingleImageBinding, AttachmentViewData>() {

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

        ImageBindingUtil.loadImage(
            binding.ivSingleImagePost,
            data.attachmentMeta.uri,
            placeholder = R.drawable.image_placeholder
        )

        binding.ivCross.setOnClickListener {

        }
    }
}