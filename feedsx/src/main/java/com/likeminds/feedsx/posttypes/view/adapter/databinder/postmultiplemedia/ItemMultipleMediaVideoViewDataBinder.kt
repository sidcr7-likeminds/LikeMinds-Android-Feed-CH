package com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemMultipleMediaVideoBinding
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_VIDEO

class ItemMultipleMediaVideoViewDataBinder(val listener: PostAdapterListener) :
    ViewDataBinder<ItemMultipleMediaVideoBinding, BaseViewType>() {
    override val viewType: Int
        get() = ITEM_MULTIPLE_MEDIA_VIDEO

    override fun createBinder(parent: ViewGroup): ItemMultipleMediaVideoBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemMultipleMediaVideoBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemMultipleMediaVideoBinding,
        data: BaseViewType,
        position: Int
    ) {
        binding.videoPost.setOnClickListener {
            val attachment = data as AttachmentViewData
            listener.postDetail(attachment.postId)
        }
    }
}