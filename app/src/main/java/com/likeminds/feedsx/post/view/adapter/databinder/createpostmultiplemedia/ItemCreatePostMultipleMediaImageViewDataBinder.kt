package com.likeminds.feedsx.post.view.adapter.databinder.createpostmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemCreatePostSingleImageBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE

class ItemCreatePostMultipleMediaImageViewDataBinder :
    ViewDataBinder<ItemCreatePostSingleImageBinding, BaseViewType>() {

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
        data: BaseViewType,
        position: Int
    ) {

        //TODO: Testing data
        ImageBindingUtil.loadImage(
            binding.ivSingleImagePost,
            "https://picsum.photos/id/237/200/300",
            placeholder = R.drawable.image_placeholder
        )
    }
}