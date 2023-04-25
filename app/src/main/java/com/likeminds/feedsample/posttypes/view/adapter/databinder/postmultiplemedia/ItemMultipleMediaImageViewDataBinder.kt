package com.likeminds.feedsample.posttypes.view.adapter.databinder.postmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsample.databinding.ItemMultipleMediaImageBinding
import com.likeminds.feedsample.posttypes.model.AttachmentViewData
import com.likeminds.feedsample.posttypes.util.PostTypeUtil
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType
import com.likeminds.feedsample.utils.model.ITEM_MULTIPLE_MEDIA_IMAGE

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
        val attachment = data as AttachmentViewData

        // loads post image inside the multiple media image view
        PostTypeUtil.initMultipleMediaImage(
            binding.ivPost,
            attachment,
        )
    }
}