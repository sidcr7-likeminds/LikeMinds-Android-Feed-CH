package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostSingleImageBinding
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_IMAGE

class ItemPostSingleImageViewDataBinder :
    ViewDataBinder<ItemPostSingleImageBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_IMAGE

    override fun createBinder(parent: ViewGroup): ItemPostSingleImageBinding {
        return ItemPostSingleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(binding: ItemPostSingleImageBinding, data: BaseViewType, position: Int) {
        //TODO: Change Implementation
        PostTypeUtil.initAuthor(
            binding.authorFrame,
            "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?auto=compress&cs=tinysrgb&w=800"
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?auto=compress&cs=tinysrgb&w=800"
        )
        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."

        ImageBindingUtil.loadImage(
            binding.ivPost,
            "https://picsum.photos/id/237/200/300",
            placeholder = R.drawable.image_placeholder
        )

        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."
    }

}