package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostLinkBinding
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_LINK

class ItemPostLinkViewDataBinder :
    ViewDataBinder<ItemPostLinkBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_LINK

    override fun createBinder(parent: ViewGroup): ItemPostLinkBinding {
        return ItemPostLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(binding: ItemPostLinkBinding, data: PostViewData, position: Int) {
        PostTypeUtil.initAuthorFrame(
            binding.authorFrame,
            data
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data
        )

        //TODO: Testing data
        binding.tvLinkDescription.text =
            "The new feature has already been implemented in the United States and Other Eu…"
        binding.tvLinkTitle.text = "Twitter will soon let you schedule your tweets"
        binding.tvLinkUrl.text = "www.youtube.com"
        binding.tvPostContent.text =
            "https://www.livemint.com/technology/tech-news/twitter-will-soon-let-you-schedule-your-tweets-11588997453592.html"
    }

}