package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostSingleImageBinding
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_IMAGE

class ItemPostSingleImageViewDataBinder :
    ViewDataBinder<ItemPostSingleImageBinding, PostViewData>() {

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
        //TODO: Change Implementation
        PostTypeUtil.initAuthorFrame(
            binding.authorFrame,
            data
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data
        )
        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."

        ImageBindingUtil.loadImage(
            binding.ivPost,
            data.attachments[0].fileUrl,
            placeholder = R.drawable.image_placeholder
        )

        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."
    }

}