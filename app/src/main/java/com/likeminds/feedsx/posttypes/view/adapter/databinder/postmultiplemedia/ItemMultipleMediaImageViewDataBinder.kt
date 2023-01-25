package com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemMultipleMediaImageBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_IMAGE

class ItemMultipleMediaImageViewDataBinder :
    ViewDataBinder<ItemMultipleMediaImageBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_MULTIPLE_MEDIA_IMAGE

    override fun createBinder(parent: ViewGroup): ItemMultipleMediaImageBinding {
        return ItemMultipleMediaImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemMultipleMediaImageBinding,
        data: BaseViewType,
        position: Int
    ) {

        //TODO: Testing data
        ImageBindingUtil.loadImage(
            binding.ivPost,
            "https://picsum.photos/id/237/200/300",
            placeholder = R.drawable.image_placeholder
        )
    }
}