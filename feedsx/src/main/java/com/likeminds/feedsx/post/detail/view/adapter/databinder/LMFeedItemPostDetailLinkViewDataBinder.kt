package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemPostDetailLinkBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_LINK

class LMFeedItemPostDetailLinkViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<LmFeedItemPostDetailLinkBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_LINK

    override fun createBinder(parent: ViewGroup): LmFeedItemPostDetailLinkBinding {
        return LmFeedItemPostDetailLinkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostDetailLinkBinding,
        data: PostViewData,
        position: Int
    ) {
        binding.apply {
            // handles various actions for the post
            PostTypeUtil.initActionsLayout(
                postActionsLayout,
                data,
                listener,
                position
            )

            // checks whether to bind complete data or not and execute corresponding lambda function
            PostTypeUtil.initPostTypeBindData(
                authorFrame,
                tvPostTitle,
                tvPostContent,
                data,
                position,
                cgTopics,
                listener,
                returnBinder = {
                    return@initPostTypeBindData
                },
                executeBinder = {
                    // handles the link view
                    val linkAttachment = data.attachments.first()
                    val ogTags = linkAttachment.attachmentMeta.ogTags
                    PostTypeUtil.initLinkView(
                        this,
                        ogTags
                    )
                }
            )
        }
    }
}