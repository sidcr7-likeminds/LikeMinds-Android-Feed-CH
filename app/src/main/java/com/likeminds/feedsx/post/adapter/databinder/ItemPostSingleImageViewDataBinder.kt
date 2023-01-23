package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostSingleImageBinding
import com.likeminds.feedsx.post.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_IMAGE

class ItemPostSingleImageViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemPostSingleImageBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_IMAGE

    override fun createBinder(parent: ViewGroup): ItemPostSingleImageBinding {
        return ItemPostSingleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(binding: ItemPostSingleImageBinding, data: PostViewData, position: Int) {
        PostTypeUtil.initAuthorFrame(
            binding.authorFrame,
            data
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data
        )

        PostTypeUtil.initTextContent(
            binding.tvPostContent,
            data,
            itemPosition = position,
            listener
        )

        ImageBindingUtil.loadImage(
            binding.ivPost,
            data.attachments.first().fileUrl,
            placeholder = R.drawable.image_placeholder
        )
    }

}