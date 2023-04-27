package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostLinkBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_LINK

class ItemPostLinkViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemPostLinkBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_LINK

    override fun createBinder(parent: ViewGroup): ItemPostLinkBinding {
        return ItemPostLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(
        binding: ItemPostLinkBinding,
        data: PostViewData,
        position: Int
    ) {
        // handles various actions for the post
        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data,
            listener,
            position
        )

        // checks whether to bind complete data or not and execute corresponding lambda function
        PostTypeUtil.initPostTypeBindData(
            binding.authorFrame,
            binding.tvPostContent,
            data,
            position,
            listener,
            returnBinder = {
                return@initPostTypeBindData
            },
            executeBinder = {
                // handles the link view
                val linkAttachment = data.attachments.first()
                val ogTags = linkAttachment.attachmentMeta.ogTags
                PostTypeUtil.initLinkView(
                    binding,
                    ogTags
                )
            }
        )
    }
}