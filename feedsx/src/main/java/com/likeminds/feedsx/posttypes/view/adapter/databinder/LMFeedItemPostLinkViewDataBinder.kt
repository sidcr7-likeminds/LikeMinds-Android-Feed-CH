package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemPostLinkBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_LINK

class LMFeedItemPostLinkViewDataBinder(
    val listener: PostAdapterListener
) : ViewDataBinder<LmFeedItemPostLinkBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_LINK

    override fun createBinder(parent: ViewGroup): LmFeedItemPostLinkBinding {
        return LmFeedItemPostLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(
        binding: LmFeedItemPostLinkBinding,
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
            data,
            position,
            binding.cgTopics,
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