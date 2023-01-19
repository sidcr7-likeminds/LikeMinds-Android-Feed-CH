package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostLinkBinding
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_LINK

class ItemPostLinkViewDataBinder :
    ViewDataBinder<ItemPostLinkBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_POST_LINK

    override fun createBinder(parent: ViewGroup): ItemPostLinkBinding {
        return ItemPostLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(binding: ItemPostLinkBinding, data: BaseViewType, position: Int) {
        //TODO: Change Implementation
        PostTypeUtil.initAuthor(
            binding.authorFrame,
            "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?auto=compress&cs=tinysrgb&w=800"
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?auto=compress&cs=tinysrgb&w=800"
        )

        binding.tvLinkDescription.text =
            "The new feature has already been implemented in the United States and Other Eu…"
        binding.tvLinkTitle.text = "Twitter will soon let you schedule your tweets"
        binding.tvLinkUrl.text = "www.youtube.com"
        binding.tvPostContent.text =
            "https://www.livemint.com/technology/tech-news/twitter-will-soon-let-you-schedule-your-tweets-11588997453592.html"
    }

}